package com.example.myfilms.data

import com.example.myfilms.data.models.*
import retrofit2.http.*

interface ApiService {

    @GET("discover/movie")
    suspend fun getMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = PARAMS_LANGUAGE,
        @Query("sort_by") sort_by: String = SORT_BY_POPULARITY,
        @Query("vote_count.gte") vote_count: Int = MIN_VOTE_COUNT_VALUE,
        @Query("page") page: Int = PARAMS_PAGE
    ): Result

    @GET("authentication/token/new")
    suspend fun getToken(
        @Query("api_key") apiKey: String = API_KEY,
    ): Token

    @POST("authentication/token/validate_with_login")
    suspend fun approveToken(
        @Query("api_key") apiKey: String = API_KEY,
        @Body loginApprove: LoginApprove
    ): Token

    @POST("authentication/session/new")
    suspend fun createSession(
        @Query("api_key") apiKey: String = API_KEY,
        @Body token: Token
    ): Session

    @Headers(
        "Accept: application/json;charset=utf-8",
        "Content-type: application/json;charset=utf-8"
    )
    @POST("account/{account_id}/favorite")
    suspend fun addFavorite(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("session_id") session_id: String = SESSION_ID,
        @Body postMovie: PostMovie
    )

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavorites(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("session_id") session_id: String = SESSION_ID,
        @Query("language") language: String = PARAMS_LANGUAGE,
        @Query("sort_by") sort_by: String = SORT_BY_POPULARITY,
        @Query("page") page: Int = PARAMS_PAGE
    ): Result

    companion object {

        private var SESSION_ID = ""
        private var API_KEY = "a14a376a2704b9c91446a56f236f5b50"
        private var PARAMS_LANGUAGE = "ru"
        private var SORT_BY_POPULARITY = "popularity.desc"
        private var MIN_VOTE_COUNT_VALUE = 1000
        private var PARAMS_PAGE = 1
    }
}