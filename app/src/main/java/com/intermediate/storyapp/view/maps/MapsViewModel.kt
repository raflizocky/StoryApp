package com.intermediate.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intermediate.storyapp.data.repository.LocationRepository
import com.intermediate.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> = _storyList

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val stories = locationRepository.getStoriesWithLocation()
                _storyList.value = stories
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}