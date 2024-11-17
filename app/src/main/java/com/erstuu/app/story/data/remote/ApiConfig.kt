package com.erstuu.app.story.data.remote

import com.erstuu.app.story.BuildConfig
import com.erstuu.app.story.data.local.preference.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL: String = BuildConfig.BASE_URL

    fun getApiService(userPreference: UserPreference): ApiService {
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val user = runBlocking {
                userPreference.getSession().first()
            }
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer ${user.token}")
                .build()
            chain.proceed(requestHeaders)
        }

        val loggingInterceptor = if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}