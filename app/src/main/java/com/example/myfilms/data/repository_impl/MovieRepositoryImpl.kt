package com.example.myfilms.data.repository_impl

import android.content.SharedPreferences
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.myfilms.data.database.MovieDao
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.data.database.model.user.AccountDetailsDbModel
import com.example.myfilms.data.database.model.user.AccountUpdateDbModel
import com.example.myfilms.data.network.ApiService
import com.example.myfilms.data.network.model.login.LoginApproveDto
import com.example.myfilms.data.network.model.login.SessionDto
import com.example.myfilms.data.network.model.login.TokenDto
import com.example.myfilms.data.network.model.movie.MovieTrailerDto
import com.example.myfilms.data.network.model.movie.PostMovieDto
import com.example.myfilms.data.network.model.user.AccountDetailsDto
import com.example.myfilms.data.paging_source.NetworkPagingSource
import com.example.myfilms.data.paging_source.RoomPagingSource
import com.example.myfilms.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class MovieRepositoryImpl(
    private val apiService: ApiService,
    private val db: MovieDao,
    private val prefSettings: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : MovieRepository {

    override fun getFavoritesFromDB(searchBy: String): Flow<PagingData<MovieDbModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RoomPagingSource(db, PAGE_SIZE, searchBy) }
        ).flow
    }

    override suspend fun getFavoriteMovieById(movieId: Int): MovieDbModel {
        return withContext(Dispatchers.Default) {
            db.getMovieById(movieId)
        }
    }

    override suspend fun deleteFavoriteMovies() {
        withContext(Dispatchers.Default) {
            db.deleteAllMovies()
        }
    }

    override suspend fun login(username: String, password: String): Int {
        return try {
            val responseGet = apiService.getToken()
            if (responseGet.isSuccessful) {
                val loginApprove = LoginApproveDto(
                    username = username,
                    password = password,
                    request_token = responseGet.body()?.request_token as String
                )
                val responseApprove = apiService.approveToken(loginApproveDto = loginApprove)
                if (responseApprove.isSuccessful) {
                    val response =
                        apiService.createSession(tokenDto = responseApprove.body() as TokenDto)
                    if (response.isSuccessful) {
                        val session = response.body()?.session_id as String
                        editor.putString(MAIN_SESSION_KEY, session).commit()
                        response.code()
                    } else {
                        response.code()
                    }
                } else {
                    responseApprove.code()
                }
            } else {
                responseGet.code()
            }
        } catch (e: Exception) {
            getErrorCode(e)
        }
    }

    override fun getMoviesFromNetwork(): Flow<PagingData<MovieDbModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NetworkPagingSource(apiService) }
        ).flow
    }

    override suspend fun getTrailer(movieId: Int): MovieTrailerDto {
        var result = MovieTrailerDto()
        try {
            val responseVideo = apiService.getTrailer(movieId)
            if (responseVideo.isSuccessful) {
                result = responseVideo.body() as MovieTrailerDto
            }
        } catch (e: Exception) {
        }
        return result
    }

    override suspend fun getFavoritesFromNetwork(): String {
        val session = getMainSession()
        var page = 1
        val movies = mutableListOf<MovieDbModel>()
        while (page != 0) {
            try {
                val response = apiService.getFavorites(session_id = session, page = page)
                if (!response.body()?.movieDbModels.isNullOrEmpty()) {
                    response.body()?.movieDbModels?.onEach {
                        movies.add(it)
                    }
                    page++
                } else {
                    page = 0
                }
            } catch (e: Exception) {
                page = 0
            }
        }
        return if (movies.isNotEmpty()) {
            db.insertMovieList(movies)
            movies.clear()
            SUCCESS
        } else {
            ERROR
        }
    }

    override suspend fun addOrDeleteFavorite(movieDbModel: MovieDbModel): Int {
        val session = getMainSession()
        val postMovie =
            PostMovieDto(media_id = movieDbModel.id as Int, isFavorite = movieDbModel.isFavorite)
        return try {
            val response = apiService.addFavorite(
                session_id = session,
                postMovieDto = postMovie
            )
            if (response.isSuccessful) {
                if (!movieDbModel.isFavorite) {
                    db.deleteMovieById(movieDbModel.id as Int)
                } else {
                    db.insertMovie(movieDbModel)
                }
                response.code()
            } else {
                response.code()
            }
        } catch (e: Exception) {
            getErrorCode(e)
        }
    }

    override suspend fun addUser() {
        val session = getMainSession()
        withContext(Dispatchers.Default) {
            try {
                val response = apiService.getAccountDetails(session_id = session)
                if (response.isSuccessful) {
                    val result = response.body() as AccountDetailsDto
                    val user = AccountDetailsDbModel(
                        id = result.id,
                        avatar = result.avatarDto?.tmdbDto?.avatarPath,
                        name = result.name,
                        username = result.username
                    )
                    val checkUser = db.getUserById(user.id as Int)
                    if (checkUser == null) {
                        db.insertUser(user)
                        editor.putInt(CURRENT_USER_ID, user.id).commit()
                    } else {
                        editor.putInt(CURRENT_USER_ID, user.id).commit()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    override suspend fun getUser(): AccountDetailsDbModel {
        val userId = getCurrentUserId()
        return db.getUserById(userId)
    }

    override suspend fun updateUser(user: AccountDetailsDbModel): Int {
        val updatedUserInfo = AccountUpdateDbModel(
            id = user.id as Int,
            name = user.name as String,
            avatar_uri = user.avatar_uri as String
        )
        return withContext(Dispatchers.Default) {
            try {
                db.userUpdate(updatedUserInfo)
                SUCCESS_CODE
            } catch (e: Exception) {
                getErrorCode(e)
            }
        }
    }

    override fun getMainSession(): String {
        return try {
            prefSettings.getString(MAIN_SESSION_KEY, "") as String
        } catch (e: Exception) {
            ERROR
        }
    }

    override suspend fun deleteMainSession() {
        val session = getMainSession()
        try {
            apiService.deleteSession(sessionDtoId = SessionDto(session_id = session))
            editor.remove(MAIN_SESSION_KEY).commit()
            deleteCurrentUserId()
        } catch (e: Exception) {
            editor.remove(MAIN_SESSION_KEY).commit()
            deleteCurrentUserId()
        }
    }

    private fun getCurrentUserId(): Int {
        return try {
            prefSettings.getInt(CURRENT_USER_ID, 0)
        } catch (e: Exception) {
            0
        }
    }

    private fun deleteCurrentUserId() {
        try {
            editor.remove(CURRENT_USER_ID).commit()
        } catch (e: Exception) {
        }
    }

    private fun getErrorCode(throwable: Throwable): Int {
        return when (throwable) {
            is HttpException -> {
                throwable.code()
            }
            is SocketTimeoutException -> {
                TIMEOUT
            }
            is IOException -> {
                NO_CONNECTION
            }
            else -> UNKNOWN_ERROR
        }
    }

//        suspend fun getAccountState(movie: Movie): Movie {
//        val session = getFragmentSession()
//        try {
//            val response =
//                apiService.getAccountStates(id = movie.id as Int, session_id = session)
//            if (response.isSuccessful) {
//                movie.isFavorite = response.body()?.favorite as Boolean
//            }
//        } catch (e: Exception) {
//        }
//        return movie
//    }

//    suspend fun updateMovie(movieId: Int, favoriteState: Boolean) {
//        val updateMovie = MovieUpdate(id = movieId, isFavorite = favoriteState)
//        withContext(Dispatchers.Default) {
//            db.movieUpdate(updateMovie)
//        }
//    }

    companion object {

        private const val MAIN_SESSION_KEY = "SESSION_MAIN"
        private const val CURRENT_USER_ID = "CURRENT_USER"
        private const val PAGE_SIZE = 20
        private const val ERROR = ""
        private const val SUCCESS = "SUCCESS"
        private const val TIMEOUT = 408
        private const val NO_CONNECTION = 502
        private const val UNKNOWN_ERROR = 0
        private const val SUCCESS_CODE = 200
    }
}