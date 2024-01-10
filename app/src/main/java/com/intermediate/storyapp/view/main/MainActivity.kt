package com.intermediate.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.intermediate.storyapp.R
import com.intermediate.storyapp.data.api.ApiConfig
import com.intermediate.storyapp.databinding.ActivityMainBinding
import com.intermediate.storyapp.view.ViewModelFactory
import com.intermediate.storyapp.view.maps.MapsActivity
import com.intermediate.storyapp.view.upload.UploadActivity
import com.intermediate.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this, ApiConfig().getApiService("token"))
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainAdapter = MainAdapter()

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin || user.token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                if (user.token.isNotEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        try {
                            viewModel.getStoryPager().collectLatest { pagingData ->
                                mainAdapter.submitData(pagingData)
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error getting stories: ${e.message}", e)
                        } finally {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                    binding.progressBar.visibility = View.GONE
                } else {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
            }
        }

        setupRecyclerView()
        playAnimation()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = mainAdapter
    }

    private fun playAnimation() {
        binding.rvUser.translationY = -300f
        binding.rvUser.alpha = 0f
        binding.fabAdd.translationY = 300f
        binding.fabAdd.alpha = 0f

        binding.rvUser.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(1000)
            .setListener(null)

        binding.fabAdd.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(1000)
            .setListener(null)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.getStories()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        performLogout()
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .create()
                alertDialog.show()
                true
            }

            R.id.action_maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                viewModel.logout()
                startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                finish()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error logging out: ${e.message}", e)
                showToast("Failed to log out. Please try again.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}