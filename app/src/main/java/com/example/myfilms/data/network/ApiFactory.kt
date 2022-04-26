package com.example.myfilms.data.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiFactory {

    companion object {

        private val LOCK = Any()
        private var apiService: ApiService? = null
        private var retrofit: Retrofit? = null
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun getInstance(): ApiService {

            apiService?.let { return it }

            synchronized(LOCK) {

                apiService?.let { return it }

                val instance = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(getOkHttp())
                    .build()
                retrofit = instance
                apiService = instance.create(ApiService::class.java)
                return apiService as ApiService
            }
        }

        private fun getOkHttp(): OkHttpClient {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(getLoggingInterceptor())
            return okHttpClient.build()
        }

        private fun getLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor(logger = object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("OkHttp", message)
                }
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }
    }
}