package com.intermediate.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.intermediate.storyapp.data.api.ApiConfig
import com.intermediate.storyapp.data.di.Injection
import com.intermediate.storyapp.data.pref.UserModel
import com.intermediate.storyapp.data.pref.UserPreference
import com.intermediate.storyapp.data.pref.dataStore
import com.intermediate.storyapp.data.repository.UserRepository
import com.intermediate.storyapp.databinding.ActivityLoginBinding
import com.intermediate.storyapp.view.ViewModelFactory
import com.intermediate.storyapp.view.main.MainActivity
import com.intermediate.storyapp.view.signup.SignupActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this, ApiConfig().getApiService("token"))
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LoginActivity", "onCreate called")
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this.dataStore)
        userRepository = Injection.provideUserRepository(this, ApiConfig().getApiService("token"))

        setupAction()
        setupView()
        checkUserSession()
        playAnimation()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                Log.d("LoginActivity", "Email and password not empty")
                binding.progressBar.visibility = View.VISIBLE
                binding.loginButton.visibility = View.INVISIBLE

                lifecycleScope.launch {
                    try {
                        val response = viewModel.login(email, password)

                        when {
                            response.loginResult != null -> {
                                val user = response.loginResult.token?.let {
                                    UserModel(email, it, true)
                                }
                                if (user != null) {
                                    viewModel.saveSession(user)
                                    withContext(Dispatchers.IO) {
                                        checkUserSession()
                                        intentMainActivity()
                                    }
                                }
                            }

                            response.message == "User not found" -> {
                                showAlertDialog(
                                    "Daftar Dulu",
                                    "Akun dengan email $email belum terdaftar. Silakan daftar terlebih dahulu."
                                ) {
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            SignupActivity::class.java
                                        )
                                    )
                                }
                            }

                            response.message == "Duplicate email" -> {
                                showAlertDialog(
                                    "Email Sudah Terdaftar",
                                    "Email dengan alamat $email telah terdaftar. Gunakan email lain atau lakukan login."
                                )
                            }

                            else -> {
                                showAlertDialog(
                                    "Login Gagal",
                                    response.message ?: "Terjadi kesalahan saat login."
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Error occurred during login: ${e.message}")
                        showAlertDialog(
                            "Login Gagal",
                            "Terjadi kesalahan saat login. Silakan coba lagi."
                        )
                    } finally {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.visibility = View.VISIBLE
                    }
                }
            } else {
                showAlertDialog("Login Gagal", "Lengkapi semua data untuk login.")
            }
        }
    }

    private fun checkUserSession() {
        Log.d("LoginActivity", "Checking user session")
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val userModel = userPreference.getSession().first()
            if (userModel.token.isNotEmpty()) {
                if (userModel.isLogin) {
                    Log.d("LoginActivity", "User session found. Navigating to main activity.")
                    intentMainActivity()
                } else {
                    Log.d("LoginActivity", "User session not found.")
                    binding.progressBar.visibility = View.GONE
                }
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun intentMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showAlertDialog(title: String, message: String, onClick: (() -> Unit)? = null) {
        AlertDialog.Builder(this@LoginActivity).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                onClick?.invoke()
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}