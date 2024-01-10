package com.intermediate.storyapp.view.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.intermediate.storyapp.data.repository.UserRepository
import com.intermediate.storyapp.data.response.ErrorResponse
import com.intermediate.storyapp.data.response.RegisterResponse
import retrofit2.HttpException

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return try {
            Log.d(
                "SignupViewModel",
                "Mengirim permintaan registrasi ke server"
            ) // Pesan log ini akan muncul saat permintaan registrasi dikirim.
            val response = repository.register(name, email, password)

            Log.d(
                "SignupViewModel",
                "Registrasi berhasil"
            ) // Pesan log ini akan muncul jika registrasi berhasil.
            response
        } catch (e: HttpException) {
            Log.e(
                "SignupViewModel",
                "Registrasi gagal",
                e
            ) // Pesan log ini akan muncul jika registrasi gagal.

            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("SignupViewModel", "Error response: $errorBody")
            throw e
        }
    }
}