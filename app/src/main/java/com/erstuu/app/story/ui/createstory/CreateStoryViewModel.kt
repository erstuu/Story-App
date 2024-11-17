package com.erstuu.app.story.ui.createstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.erstuu.app.story.data.UserRepository
import com.erstuu.app.story.models.User
import java.io.File

class CreateStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }

    fun uploadImage(
        file: File,
        description: String,
        latitude: Float?,
        longitude: Float?
    ) = repository.uploadImage(file, description, latitude, longitude)
}