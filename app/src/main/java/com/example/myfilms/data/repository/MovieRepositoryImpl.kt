package com.example.myfilms.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.paging.*
import com.example.myfilms.data.database.MovieDao
import com.example.myfilms.data.database.MovieDatabase
import com.example.myfilms.data.models.account.AccountDetails
import com.example.myfilms.data.models.account.AccountUpdate
import com.example.myfilms.data.models.account.DbAccountDetails
import com.example.myfilms.data.models.authorization.LoginApprove
import com.example.myfilms.data.models.authorization.Session
import com.example.myfilms.data.models.authorization.Token
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.models.movie.MovieVideos
import com.example.myfilms.data.models.movie.PostMovie
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.network.ApiService
import com.example.myfilms.data.paging_source.NetworkPagingSource
import com.example.myfilms.data.paging_source.RoomPagingSource
import com.example.myfilms.domain.MovieRepository
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.lang.Exception

class MovieRepositoryImpl(
    private val apiService: ApiService,
    private val db: MovieDao,
    private val prefSettings: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : MovieRepository {

    //работа с Room
    override fun getFavoritesFromDB(searchBy: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RoomPagingSource(db, PAGE_SIZE, searchBy) }
        ).flow
    }

    override suspend fun getFavoriteMovieById(movieId: Int): Movie {
        return withContext(Dispatchers.Default) {
            db.getMovieById(movieId)
        }
    }

    override suspend fun deleteFavoriteMovies() {
        withContext(Dispatchers.Default) {
            db.deleteAllMovies()
        }
    }

    //работа с Retrofit и Room
    override suspend fun login(username: String, password: String): String {
        var session = ""
        try {
            val responseGet = apiService.getToken()
            if (responseGet.isSuccessful) {
                val loginApprove = LoginApprove(
                    username = username,
                    password = password,
                    request_token = responseGet.body()?.request_token as String
                )
                val responseApprove = apiService.approveToken(loginApprove = loginApprove)
                if (responseApprove.isSuccessful) {
                    val response = apiService.createSession(token = responseApprove.body() as Token)
                    if (response.isSuccessful) {
                        session = response.body()?.session_id as String
                        editor.putString(MAIN_SESSION_KEY, session).commit()
                        editor.putString(LOGIN_SESSION_KEY, "Access").commit()
                    }
                }
            }
        } catch (e: Exception) {
        }
        return session
    }

    override fun getMoviesFromNetwork(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NetworkPagingSource(apiService) }
        ).flow
    }

    override suspend fun getTrailer(movieId: Int): MovieVideos {
        var result = MovieVideos()
        try {
            val responseVideo = apiService.getTrailer(movieId)
            if (responseVideo.isSuccessful) {
                result = responseVideo.body() as MovieVideos
            }
        } catch (e: Exception) {
        }
        return result
    }

    override suspend fun getFavoritesFromNetwork() {
        val session = getMainSession()
        var page = 1
        val movies = mutableListOf<Movie>()
        while (page != 0) {
            try {
                val response = apiService.getFavorites(session_id = session, page = page)
                if (!response.body()?.movies.isNullOrEmpty()) {
                    response.body()?.movies?.onEach {
                        movies.add(it)
                    }
                    page++
                } else {
                    page = 0
                }
            } catch (e: Exception) {
            }
        }
        if (!movies.isNullOrEmpty()) {
            db.insertMovieList(movies)
            movies.clear()
        }
    }

    override suspend fun addOrDeleteFavorite(movie: Movie): LoadingState {
        var loadingState = LoadingState.FINISHED
        val session = getMainSession()
        val postMovie = PostMovie(media_id = movie.id as Int, isFavorite = movie.isFavorite)
        try {
            val response = apiService.addFavorite(
                session_id = session,
                postMovie = postMovie
            )
            if (response.isSuccessful) {
                if (!movie.isFavorite) {
                    db.deleteMovieById(movie.id as Int)
                } else {
                    db.insertMovie(movie)
                }
                loadingState = LoadingState.SUCCESS
            }
        } catch (e: Exception) {
        }
        return loadingState
    }

    override suspend fun addUser() {
        val session = getMainSession()
        withContext(Dispatchers.Default) {
            try {
                val response = apiService.getAccountDetails(session_id = session)
                if (response.isSuccessful) {
                    val result = response.body() as AccountDetails
                    val user = DbAccountDetails(
                        id = result.id,
                        avatar = result.avatar?.tmdb?.avatarPath,
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

    override suspend fun getUser(): DbAccountDetails {
        val userId = getCurrentUserId()
        return db.getUserById(userId)
    }

    override suspend fun updateUser(accountId: Int, name: String, uri: String): LoadingState {
        var loadingState = LoadingState.FINISHED
        val updateAccount = AccountUpdate(id = accountId, name = name, avatar_uri = uri)
        withContext(Dispatchers.Default) {
            try {
                db.userUpdate(updateAccount)
                loadingState = LoadingState.SUCCESS
            } catch (e: Exception) {
            }
        }
        return loadingState
    }

    //работа с Shared Preference
    override fun getMainSession(): String {
        var session = ""
        try {
            session = prefSettings.getString(MAIN_SESSION_KEY, "") as String
        } catch (e: Exception) {
        }
        return session
    }

    override fun getLoginSession(): String {
        var session = ""
        try {
            session = prefSettings.getString(LOGIN_SESSION_KEY, "") as String
        } catch (e: Exception) {
        }
        return session
    }

    private fun getCurrentUserId(): Int {
        return try {
            prefSettings.getInt(CURRENT_USER_ID, 0)
        } catch (e: Exception) {
            return 0
        }
    }

    override suspend fun deleteMainSession() {
        val session = getMainSession()
        try {
            apiService.deleteSession(sessionId = Session(session_id = session))
            editor.remove(MAIN_SESSION_KEY).commit()
            deleteCurrentUserId()
        } catch (e: Exception) {
            editor.remove(MAIN_SESSION_KEY).commit()
            deleteCurrentUserId()
        }
    }

    override fun deleteLoginSession() {
        try {
            editor.remove(LOGIN_SESSION_KEY).commit()
        } catch (e: Exception) {
        }
    }

    private fun deleteCurrentUserId() {
        try {
            editor.remove(CURRENT_USER_ID).commit()
        } catch (e: Exception) {
        }
    }

    //    suspend fun getAccountState(movie: Movie): Movie {
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
        private const val LOGIN_SESSION_KEY = "SESSION_LOGIN"
        private const val CURRENT_USER_ID = "CURRENT_USER"
        private const val PAGE_SIZE = 20
    }
}