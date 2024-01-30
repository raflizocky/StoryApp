package com.intermediate.storyapp.view.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.intermediate.storyapp.R
import com.intermediate.storyapp.data.api.ApiConfig
import com.intermediate.storyapp.data.di.Injection
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.pref.dataStore
import com.intermediate.storyapp.data.repository.UserRepository
import com.intermediate.storyapp.databinding.ActivityMapsBinding
import com.intermediate.storyapp.view.ViewModelFactory
import com.intermediate.storyapp.view.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this, ApiConfig().getApiService("token"))
    }
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var userPreference: UserPreference
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this.dataStore)
        userRepository = Injection.provideUserRepository(this, ApiConfig().getApiService("token"))

        checkUserSession()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // setup map UI
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()

        // Add some initial markers
        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        mMap.addMarker(
            MarkerOptions()
                .position(dicodingSpace)
                .title("Dicoding Space")
                .snippet("Batik Kumeli No.50")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))

        // Set on click listener for points of interest
        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }
    }

    // Function to check user session
    private fun checkUserSession() {
        Log.d("MapsActivity", "Checking user session")
        lifecycleScope.launch {
            val userModel = userPreference.getSession().first()
            if (userModel.token.isNotEmpty()) {
                observeViewModel()
                viewModel.getStoriesWithLocation()
            } else {
                Log.d("MapsActivity", "Failed checkuserSession()")
                showToast("Please try again.")
                startActivity(Intent(this@MapsActivity, MainActivity::class.java))
            }
        }
    }

    // Function to observe the view model and handle updates
    private fun observeViewModel() {
        viewModel.storyList.observe(this) { listStory ->
            listStory.forEach { data ->
                val latLng = LatLng(data.lat ?: 0.0, data.lon ?: 0.0)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(data.name)
                        .snippet(data.description)
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}