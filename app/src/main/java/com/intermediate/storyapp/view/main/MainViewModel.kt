package com.intermediate.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.intermediate.storyapp.data.pref.UserModel
import com.intermediate.storyapp.data.repository.StoryRepository
import com.intermediate.storyapp.data.repository.UserRepository
import com.intermediate.storyapp.data.response.ListStoryItem
import com.intermediate.storyapp.data.response.StoryDetailResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _storyList = MutableLiveData<List<ListStoryItem>>()

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    suspend fun getStories() {
        try {
            val response = storyRepository.getStories()
            _storyList.postValue(response.listStory)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getStoryById(storyId: String): StoryDetailResponse {
        try {
            return storyRepository.getStoryById(storyId)
        } catch (e: Exception) {
            throw e
        }
    }

    fun getStoryPager(): Flow<PagingData<ListStoryItem>> {
        return storyRepository.getStoryPager()
    }
}