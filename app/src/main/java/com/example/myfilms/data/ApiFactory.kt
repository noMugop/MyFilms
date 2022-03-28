package com.example.myfilms.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiFactory {

    companion object {

        public fun getInstance(): ApiService {

            apiService?.let { return it }

            synchronized(LOCK) {

                apiService?.let { return it }

                val instance = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()

                retrofit = instance
                apiService = instance.create(ApiService::class.java)
                return apiService as ApiService
            }

        }

        private var apiService: ApiService? = null

        private var retrofit: Retrofit? = null

        private const val BASE_URL = "https://api.themoviedb.org/3/discover/"

        private val LOCK = Any()
    }
}