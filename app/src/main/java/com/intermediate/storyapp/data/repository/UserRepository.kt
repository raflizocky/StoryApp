package com.intermediate.storyapp.data.repository

import android.util.Log
import com.google.gson.Gson
import com.intermediate.storyapp.data.api.ApiService
import com.intermediate.storyapp.data.pref.UserModel
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.response.ErrorResponse
import com.intermediate.storyapp.data.response.LoginResponse
import com.intermediate.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return try {
            val response = apiService.register(name, email, password)
            Log.d("UserRepository", "Registration successful. Response: $response")
            response
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("UserRepository", "Registration failed. Error: ${errorBody.message}")
            throw e
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            apiService.login(email, password)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("UserRepository", "Registration failed: $errorBody")
            throw e
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}