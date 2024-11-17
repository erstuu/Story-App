package com.erstuu.app.story.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.erstuu.app.story.data.local.StoryDatabase
import com.erstuu.app.story.models.User
import com.erstuu.app.story.data.local.preference.UserPreference
import com.erstuu.app.story.data.paging.StoriesRemoteMediator
import com.erstuu.app.story.data.remote.ApiService
import com.erstuu.app.story.data.response.DetailStoryResponse
import com.erstuu.app.story.data.response.ErrorResponse
import com.erstuu.app.story.data.response.ListStoryItem
import com.erstuu.app.story.models.Stories
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val database: StoryDatabase
) {

    suspend fun saveSession(user: User) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<User> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun registerUser(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.userRegisterRequest(name, email, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultState.Error(errorMessage.toString()))
        }
    }

    fun loginUser(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.userLoginRequest(email, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(ResultState.Error(errorMessage.toString()))
        }
    }

    fun getStories(): LiveData<PagingData<Stories>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoriesRemoteMediator(database, apiService),
            pagingSourceFactory = {
                database.storyDao().getAllStories()
            }
        ).liveData
    }

    fun getStoriesById(id: String): LiveData<ResultState<DetailStoryResponse>> = liveData {
        emit(ResultState.Loading)
        val response = apiService.getDetailStory(id)
        if (response.error == false) {
            emit(ResultState.Success(response))
        } else {
            emit(ResultState.Error(response.message.toString()))
        }
    }

    fun getStoriesWithLocation(): LiveData<ResultState<List<ListStoryItem>>> = liveData {
        emit(ResultState.Loading)
        val response = apiService.getStoriesWithLocation()
        if (response.error == false) {
            emit(ResultState.Success(response.listStory))
        } else {
            emit(ResultState.Error(response.message.toString()))
        }
    }

    fun uploadImage(
        imageFile: File,
        description: String,
        latitude: Float?,
        longitude: Float?
    ): LiveData<ResultState<ErrorResponse>> = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        try {
            val successResponse = apiService.uploadStory(multipartBody, requestBody, latitude, longitude)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(ResultState.Error(errorResponse.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            database: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService, database)
            }.also { instance = it }
    }
}