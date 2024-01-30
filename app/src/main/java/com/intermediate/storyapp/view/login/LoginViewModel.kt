package com.intermediate.storyapp.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.intermediate.storyapp.data.pref.UserModel
import com.intermediate.storyapp.data.repository.UserRepository
import com.intermediate.storyapp.data.response.LoginResponse

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            Log.d("LoginViewModel", "Initiating login request for email: $email")
            val response = repository.login(email, password)
            Log.d("LoginViewModel", "Login response received: $response")

            if (response.loginResult != null) {
                response.loginResult.token?.let { token ->
                    val user = UserModel(email, token, true)
                    saveSession(user)
                }
            }
            response
        } catch (e: Exception) {
            Log.e("LoginViewModel", "An error occurred during login: ${e.message}")
            throw e
        }
    }

    suspend fun saveSession(user: UserModel) {
        try {
            repository.saveSession(user)
            Log.d("LoginViewModel", "User session saved successfully")
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error occurred while saving user session: ${e.message}")
            throw e
        }
    }
}