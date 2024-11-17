package com.erstuu.app.story.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.erstuu.app.story.data.UserRepository
import com.erstuu.app.story.models.User

class MapViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }

    fun getStoriesWithLocation() = repository.getStoriesWithLocation()
}