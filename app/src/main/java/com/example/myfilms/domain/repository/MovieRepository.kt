package com.example.myfilms.domain.repository

import androidx.paging.PagingData
import com.example.myfilms.data.database.model.user.AccountDetailsDbModel
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.data.network.model.movie.MovieTrailerDto
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getFavoritesFromDB(searchBy: String): Flow<PagingData<MovieDbModel>>
    suspend fun getFavoriteMovieById(movieId: Int): MovieDbModel
    suspend fun deleteFavoriteMovies()

    suspend fun login(username: String, password: String): String
    fun getMoviesFromNetwork(): Flow<PagingData<MovieDbModel>>
    suspend fun getTrailer(movieId: Int): MovieTrailerDto
    suspend fun getFavoritesFromNetwork()
    suspend fun addOrDeleteFavorite(movieDbModel: MovieDbModel): LoadingState
    suspend fun addUser()
    suspend fun getUser(): AccountDetailsDbModel
    suspend fun updateUser(user: AccountDetailsDbModel): LoadingState

    fun getMainSession(): String
    suspend fun deleteMainSession()
    fun getLoginSession(): String
    fun deleteLoginSession()
}