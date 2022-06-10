package com.example.myfilms.domain.repository

import androidx.paging.PagingData
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.data.database.model.user.AccountDetailsDbModel
import com.example.myfilms.data.network.model.movie.MovieTrailerDto
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getFavoritesFromDB(searchBy: String): Flow<PagingData<MovieDbModel>>
    suspend fun getFavoriteMovieById(movieId: Int): MovieDbModel
    suspend fun deleteFavoriteMovies()

    suspend fun login(username: String, password: String): Int
    fun getMoviesFromNetwork(): Flow<PagingData<MovieDbModel>>
    suspend fun getTrailer(movieId: Int): MovieTrailerDto
    suspend fun getFavoritesFromNetwork()
    suspend fun addOrDeleteFavorite(movieDbModel: MovieDbModel): Int
    suspend fun addUser()
    suspend fun getUser(): AccountDetailsDbModel
    suspend fun updateUser(user: AccountDetailsDbModel): Int

    fun getMainSession(): String
    suspend fun deleteMainSession()
}