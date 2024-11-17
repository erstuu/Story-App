package com.erstuu.app.story.di

import android.content.Context
import com.erstuu.app.story.data.UserRepository
import com.erstuu.app.story.data.local.StoryDatabase
import com.erstuu.app.story.data.local.preference.UserPreference
import com.erstuu.app.story.data.local.preference.dataStore
import com.erstuu.app.story.data.remote.ApiConfig
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val database = StoryDatabase.getDatabase(context)

        return UserRepository.getInstance(pref, apiService, database)
    }
}