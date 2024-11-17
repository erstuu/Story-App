package com.erstuu.app.story.ui.login

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
import com.erstuu.app.story.models.User
import com.erstuu.app.story.databinding.ActivityLoginBinding
import com.erstuu.app.story.ui.ViewModelFactory
import com.erstuu.app.story.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text?.toString()?.trim()
            val password = binding.passwordEditText.text?.toString()?.trim()

            if (email.isNullOrEmpty()) {
                showToast(getString(R.string.email_null))
                return@setOnClickListener
            }

            if (password.isNullOrEmpty()) {
                showToast(getString(R.string.password_error))
                return@setOnClickListener
            }

            viewModel.userLoginAccount(email, password).observe(this) { response ->
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
                        val user = User(
                            email,
                            response.data.loginResult?.token.toString(),
                            true
                        )
                        viewModel.saveSession(user)
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle(getString(R.string.congratulation))
                            setMessage(getString(R.string.login_success))
                            setPositiveButton(getString(R.string.next)) { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
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