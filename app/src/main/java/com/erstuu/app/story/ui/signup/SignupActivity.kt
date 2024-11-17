package com.erstuu.app.story.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.erstuu.app.story.R
import com.erstuu.app.story.data.ResultState
import com.erstuu.app.story.databinding.ActivitySignupBinding
import com.erstuu.app.story.ui.ViewModelFactory
import com.erstuu.app.story.ui.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text?.toString()?.trim()
            val email = binding.emailEditText.text?.toString()?.trim()
            val password = binding.passwordEditText.text?.toString()?.trim()

            if (name.isNullOrEmpty()) {
                showToast(getString(R.string.name_error))
                return@setOnClickListener
            }

            if (email.isNullOrEmpty()) {
                showToast(getString(R.string.email_error))
                return@setOnClickListener
            }

            if (password.isNullOrEmpty()) {
                showToast(getString(R.string.password_error))
                return@setOnClickListener
            }

            viewModel.userRegisterAccount(name, email, password).observe(this) { response ->
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
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.yeah))
                            setMessage(getString(R.string.account_registered))
                            setPositiveButton(R.string.next) { _, _ ->
                                val intent = Intent(context, LoginActivity::class.java)
                                startActivity(intent)
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}