package com.intermediate.storyapp.view.upload

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import android.Manifest
import com.intermediate.storyapp.R
import com.intermediate.storyapp.data.api.ApiConfig
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.pref.dataStore
import com.intermediate.storyapp.data.response.UploadResponse
import com.intermediate.storyapp.databinding.ActivityUploadBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var userPreference: UserPreference
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this.dataStore)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()

            showLoading(true)

            lifecycleScope.launch {
                val token = withContext(Dispatchers.IO) {
                    userPreference.getSession().firstOrNull()?.token
                }

                token?.let { it ->
                    val requestBody = description.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "photo",
                        imageFile.name,
                        requestImageFile
                    )
                    try {
                        val apiService = ApiConfig().getApiService(it)
                        Log.d("PhotoActivity", "Request: Sending upload request to server")
                        val successResponse = apiService.uploadImage(multipartBody, requestBody)
                        successResponse.message?.let {
                            showToast(it)
                            Log.d("PhotoActivity", "Response message: $it")
                        }
                        finish()
                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
                        errorResponse.message?.let {
                            showToast(it)
                            Log.e("PhotoActivity", "Error message: $it")
                        }
                    } finally {
                        showLoading(false)
                    }
                } ?: showToast(getString(R.string.empty_image_warning))
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}