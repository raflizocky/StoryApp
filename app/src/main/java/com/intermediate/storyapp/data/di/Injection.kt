package com.intermediate.storyapp.data.di

import android.content.Context
import com.intermediate.storyapp.data.api.ApiConfig
import com.intermediate.storyapp.data.api.ApiService
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.pref.dataStore
import com.intermediate.storyapp.data.repository.LocationRepository
import com.intermediate.storyapp.data.repository.StoryRepository
import com.intermediate.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context, apiService: ApiService): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig().getApiService(user.token)
        return StoryRepository.getInstance(apiService, pref)
    }

    fun provideLocationRepository(context: Context): LocationRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig().getApiService(user.token)
        return LocationRepository.getInstance(apiService, pref)
    }
}