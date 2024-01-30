package com.intermediate.storyapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.intermediate.storyapp.data.StoryPagingSource
import com.intermediate.storyapp.data.api.ApiService
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.response.ListStoryItem
import com.intermediate.storyapp.data.response.StoryDetailResponse
import com.intermediate.storyapp.data.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getStories(): StoryResponse {
        try {
            userPreference.getSession().firstOrNull()?.token
                ?: throw NullPointerException("Token is null")
            return apiService.getStories()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getStoryById(storyId: String): StoryDetailResponse {
        try {
            userPreference.getSession().firstOrNull()?.token
                ?: throw NullPointerException("Token is null")
            return apiService.getStoryById(storyId)
        } catch (e: Exception) {
            throw e
        }
    }

    fun getStoryPager(): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).flow
    }

    companion object {
        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository {
            return StoryRepository(apiService, userPreference)
        }
    }
}