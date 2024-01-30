package com.intermediate.storyapp.view.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.intermediate.storyapp.data.api.ApiConfig
import com.intermediate.storyapp.data.response.StoryDetailResponse
import com.intermediate.storyapp.databinding.ActivityDetailBinding
import com.intermediate.storyapp.view.ViewModelFactory
import com.intermediate.storyapp.view.main.MainViewModel
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this, ApiConfig().getApiService("token"))
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("STORY_ID") ?: ""
        if (storyId.isNotEmpty()) {
            fetchStoryDetails(storyId)
        } else {
            Log.e("DetailActivity", "Story ID not found")
        }
    }

    private fun fetchStoryDetails(storyId: String) {
        lifecycleScope.launch {
            try {
                val response = viewModel.getStoryById(storyId)
                updateUI(response)
            } catch (e: Exception) {
                Log.e("DetailActivity", "Error fetching story details: ${e.message}", e)
            }
        }
    }

    private fun updateUI(storyResponse: StoryDetailResponse) {
        val story = storyResponse.story
        if (story != null) {
            binding?.apply {
                tvItemNameDesc.text = story.name
                tvItemDescriptionDesc.text = story.description

                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .into(imgItemPhotoDesc)
            }
        } else {
            Log.e("DetailActivity", "Story details not found")
        }
    }
}