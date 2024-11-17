package com.erstuu.app.story.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erstuu.app.story.data.UserRepository
import com.erstuu.app.story.models.User
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: User) {
        viewModelScope.launch {
            repository.saveSession(user)
        }

    }

    fun userLoginAccount(email: String, password: String) =
        repository.loginUser(email, password)
}