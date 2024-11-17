package com.erstuu.app.story.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.erstuu.app.story.R
import com.erstuu.app.story.databinding.ActivityMainBinding
import com.erstuu.app.story.ui.ViewModelFactory
import com.erstuu.app.story.ui.adapter.LoadingStateAdapter
import com.erstuu.app.story.ui.adapter.StoryAdapter
import com.erstuu.app.story.ui.createstory.CreateStoryActivity
import com.erstuu.app.story.ui.map.MapsActivity
import com.erstuu.app.story.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                getStories()
            }
        }

        setupRecyclerView()
        setupActionMenu()
        setupAddStory()
    }

    private fun setupActionMenu() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.logout))
                        .setMessage(getString(R.string.logout_acc))
                        .setPositiveButton(getString(R.string.confirm_logout)) { _, _ ->
                            viewModel.logout()
                        }
                        .setNegativeButton(getString(R.string.confirm_not_logout)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    true
                }

                R.id.menu_change_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                R.id.menu_map -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = storyAdapter
    }

    private fun getStories() {
        binding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        viewModel.getStories().observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun setupAddStory() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, CreateStoryActivity::class.java))
        }
    }
}