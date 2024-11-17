package com.erstuu.app.story.data.remote

import com.erstuu.app.story.data.response.DetailStoryResponse
import com.erstuu.app.story.data.response.ErrorResponse
import com.erstuu.app.story.data.response.StoriesResponse
import com.erstuu.app.story.data.response.UserLoginResponse
import com.erstuu.app.story.data.response.UserRegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun userLoginRequest(
        @Field("email") email: String,
        @Field("password") password: String
    ): UserLoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun userRegisterRequest(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): UserRegisterResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoriesResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ): DetailStoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float? = null,
        @Part("lon") lon: Float? = null
    ): ErrorResponse
}