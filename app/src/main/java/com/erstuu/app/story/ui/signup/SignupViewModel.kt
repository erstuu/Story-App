package com.erstuu.app.story.ui.signup

import androidx.lifecycle.ViewModel
import com.erstuu.app.story.data.UserRepository

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    fun userRegisterAccount(name: String, email: String, password: String) =
        repository.registerUser(name, email, password)
}