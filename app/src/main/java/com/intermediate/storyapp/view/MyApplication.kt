package com.intermediate.storyapp.view

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // For forcing Light Mode always
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // For forcing Dark Mode always
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}