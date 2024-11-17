package com.erstuu.app.story.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.erstuu.app.story.data.UserRepository
import com.erstuu.app.story.models.User

class DetailStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }

    fun getDetailStory(id: String) = repository.getStoriesById(id)
}