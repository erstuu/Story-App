package com.erstuu.app.story.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.erstuu.app.story.R
import com.erstuu.app.story.data.ResultState
import com.erstuu.app.story.databinding.ActivityDetailStoryBinding
import com.erstuu.app.story.ui.ViewModelFactory
import com.erstuu.app.story.ui.welcome.WelcomeActivity


class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    private val viewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSession()

        val toolbar: Toolbar = findViewById<View>(R.id.topAppBar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupAction(intent.getStringExtra(ID) ?: "")
            }
        }
    }

    private fun setupAction(id: String) {
        viewModel.getDetailStory(id).observe(this) { response ->
            when (response) {
                ResultState.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is ResultState.Error -> {
                    binding.progressBar.isVisible = false
                    showToast(response.error)
                }

                is ResultState.Success -> {
                    binding.progressBar.isVisible = false
                    Glide.with(this)
                        .load(response.data.story.photoUrl)
                        .placeholder(R.drawable.image_placeholder)
                        .into(binding.imgStory)

                    binding.storyName.text = response.data.story.name
                    binding.storyDescription.text = response.data.story.description
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ID = "id"
    }
}