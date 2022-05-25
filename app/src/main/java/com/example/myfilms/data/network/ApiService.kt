package com.example.myfilms.data.network

import com.example.myfilms.data.network.model.user.AccountDetailsDto
import com.example.myfilms.data.network.model.login.LoginApproveDto
import com.example.myfilms.data.network.model.login.SessionDto
import com.example.myfilms.data.network.model.login.TokenDto
import com.example.myfilms.data.network.model.movie.AccountStatesDto
import com.example.myfilms.data.network.model.movie.PostMovieDto
import com.example.myfilms.data.network.model.movie.ResultFavoriteDto
import com.example.myfilms.data.network.model.movie.ResultMoviesDto
import com.example.myfilms.data.network.model.movie.MovieTrailerDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("discover/movie")
    suspend fun getMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = PARAMS_LANGUAGE,
        @Query("sort_by") sort_by: String = SORT_BY_POPULARITY,
        @Query("page") page: Int = PARAMS_PAGE
    ):Response<ResultMoviesDto>

//    @GET("movie/{movie_id}")
//    suspend fun getMovieById(
//        @Path("movie_id") id: Int,
//        @Query("api_key") apiKey: String = API_KEY,
//        @Query("language") language: String = PARAMS_LANGUAGE
//    ): Response<Movie>

    @GET("movie/{movie_id}/videos")
    suspend fun getTrailer(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = PARAMS_LANGUAGE
    ): Response<MovieTrailerDto>

    @GET("authentication/token/new")
    suspend fun getToken(
        @Query("api_key") apiKey: String = API_KEY,
    ): Response<TokenDto>

    @POST("authentication/token/validate_with_login")
    suspend fun approveToken(
        @Query("api_key") apiKey: String = API_KEY,
        @Body loginApproveDto: LoginApproveDto
    ): Response<TokenDto>

    @POST("authentication/session/new")
    suspend fun createSession(
        @Query("api_key") apiKey: String = API_KEY,
        @Body tokenDto: TokenDto
    ): Response<SessionDto>

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(
        @Query("api_key") apiKey: String = API_KEY,
        @Body sessionDtoId: SessionDto
    )

    @Headers(
        "Accept: application/json;charset=utf-8",
        "Content-type: application/json;charset=utf-8"
    )
    @POST("account/{account_id}/favorite")
    suspend fun addFavorite(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("session_id") session_id: String = SESSION_ID,
        @Body postMovieDto: PostMovieDto
    ): Response<ResultFavoriteDto>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavorites(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("session_id") session_id: String = SESSION_ID,
        @Query("language") language: String = PARAMS_LANGUAGE,
        @Query("sort_by") sort_by: String = SORT_BY_POPULARITY,
        @Query("page") page: Int = PARAMS_PAGE
    ): Response<ResultMoviesDto>

    @GET("movie/{movie_id}/account_states")
    suspend fun getAccountStates(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("session_id") session_id: String = SESSION_ID
    ): Response<AccountStatesDto>

    @GET("account")
    suspend fun getAccountDetails(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("session_id") session_id: String = SESSION_ID
    ): Response<AccountDetailsDto>

    companion object {

        private var SESSION_ID = ""
        private var API_KEY = "a14a376a2704b9c91446a56f236f5b50"
        private var PARAMS_LANGUAGE = "ru"
        private var SORT_BY_POPULARITY = "popularity.desc"
        private var PARAMS_PAGE = 1
    }
}