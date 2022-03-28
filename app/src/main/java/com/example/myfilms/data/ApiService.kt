package com.example.myfilms.data

import com.example.myfilms.presentation.models.Result
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("movie")
    suspend fun getMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = PARAMS_LANGUAGE,
        @Query("sort_by") sort_by: String = SORT_BY_POPULARITY,
        @Query("vote_count.gte") vote_count: Int = MIN_VOTE_COUNT_VALUE,
        @Query("page") page: Int = PARAMS_PAGE
    ): Result

    companion object {

        private var API_KEY = "a14a376a2704b9c91446a56f236f5b50"
        private var PARAMS_LANGUAGE = "ru"
        private var SORT_BY_POPULARITY = "popularity.desc"
        private var MIN_VOTE_COUNT_VALUE = 1000
        private var PARAMS_PAGE = 1
    }
}