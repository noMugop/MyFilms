package com.example.myfilms.domain

import androidx.paging.PagingData
import com.example.myfilms.data.models.account.DbAccountDetails
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.models.movie.MovieVideos
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getFavoritesFromDB(searchBy: String): Flow<PagingData<Movie>>
    suspend fun getFavoriteMovieById(movieId: Int): Movie
    suspend fun deleteFavoriteMovies()

    suspend fun login(username: String, password: String): String
    fun getMoviesFromNetwork(): Flow<PagingData<Movie>>
    suspend fun getTrailer(movieId: Int): MovieVideos
    suspend fun getFavoritesFromNetwork()
    suspend fun addOrDeleteFavorite(movie: Movie): LoadingState
    suspend fun addUser()
    suspend fun getUser(): DbAccountDetails
    suspend fun updateUser(accountId: Int, name: String, uri: String): LoadingState

    fun getMainSession(): String
    suspend fun deleteMainSession()
    fun getLoginSession(): String
    fun deleteLoginSession()
}