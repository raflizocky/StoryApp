package com.intermediate.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.intermediate.storyapp.data.api.ApiService
import com.intermediate.storyapp.data.di.Injection
import com.intermediate.storyapp.data.repository.LocationRepository
import com.intermediate.storyapp.data.repository.StoryRepository
import com.intermediate.storyapp.data.repository.UserRepository
import com.intermediate.storyapp.view.login.LoginViewModel
import com.intermediate.storyapp.view.main.MainViewModel
import com.intermediate.storyapp.view.maps.MapsViewModel
import com.intermediate.storyapp.view.signup.SignupViewModel

class ViewModelFactory(
    private val repository: UserRepository,
    private val storyRepository: StoryRepository,
    private val locationRepository: LocationRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository, storyRepository) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository) as T
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> SignupViewModel(repository) as T
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> MapsViewModel(locationRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context, apiService: ApiService): ViewModelFactory {
            clearInstance()

            synchronized(ViewModelFactory::class.java) {
                val userRepository = Injection.provideUserRepository(context, apiService)
                val storyRepository = Injection.provideStoryRepository(context)
                val locationRepository = Injection.provideLocationRepository(context)
                INSTANCE = ViewModelFactory(userRepository, storyRepository, locationRepository)
            }

            return INSTANCE as ViewModelFactory
        }

        @JvmStatic
        private fun clearInstance() {
            INSTANCE = null
        }
    }
}