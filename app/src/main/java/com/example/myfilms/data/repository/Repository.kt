package com.example.myfilms.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.myfilms.data.database.MovieDatabase
import com.example.myfilms.data.models.*
import com.example.myfilms.data.models.account.AccountDetails
import com.example.myfilms.data.models.account.DbAccountDetails
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class Repository(application: Application) {

    private val apiService = ApiFactory.getInstance()
    private val db = MovieDatabase.getInstance(application).movieDao()
    private var prefSettings: SharedPreferences = application.getSharedPreferences(
        APP_SETTINGS,
        Context.MODE_PRIVATE
    ) as SharedPreferences
    private var editor: SharedPreferences.Editor = prefSettings.edit()

    suspend fun getMovieList(): List<Movie> {
        return withContext(Dispatchers.Default) {
            db.getMovieList()
        }
    }

    suspend fun getMovieById(movieId: Int): Movie {
        return withContext(Dispatchers.Default) {
            db.getMovieById(movieId)
        }
    }

    suspend fun loadData(page: Int): LoadingState {
        return withContext(Dispatchers.Default) {
            try {
                val response = apiService.getMovies(page = page)
                if (response.isSuccessful) {
                    val result = response.body()?.movies
                    if (!result.isNullOrEmpty()) {
                        db.insertMovieList(result)
                    }
                }
            } catch (e: Exception) {
            }
            LoadingState.SUCCESS
        }
    }

    suspend fun syncFavorites(page: Int) {
        val session = getFragmentSession()
        withContext(Dispatchers.Default) {
            try {
                val response = apiService.getFavorites(session_id = session, page = page)
                if (response.isSuccessful) {
                    val result = response.body()?.movies as List<Movie>
                    result.map {
                        updateMovie(it.id as Int, true)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    suspend fun updateMovie(movieId: Int, favoriteState: Boolean) {
        val updateMovie = MovieUpdate(id = movieId, isFavorite = favoriteState)
        withContext(Dispatchers.Default) {
            db.update(updateMovie)
        }
    }

    suspend fun getTrailer(movieId: Int): MovieVideos {
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

    suspend fun login(username: String, password: String): String {
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
                        editor.putString(FRAGMENTS_KEY, session).commit()
                        editor.putString(LOGIN_KEY, "Access").commit()
                    }
                }
            }
        } catch (e: Exception) {
        }
        return session
    }

    private fun getFragmentSession(): String {
        var session = ""
        try {
            session = prefSettings.getString(FRAGMENTS_KEY, "") as String
        } catch (e: Exception) {
        }
        return session
    }

    private fun getLoginSession(): String {
        var session = ""
        try {
            session = prefSettings.getString(LOGIN_KEY, "") as String
        } catch (e: Exception) {
        }
        return session
    }

    private fun getCurrentUserId(): Int {
        var userId = 0
        try {
            userId = prefSettings.getInt(CURRENT_USER_ID, 0)
        } catch (e: Exception) {
        }
        return userId
    }

    fun checkFragmentSession(): String {
        return getFragmentSession()
    }

    fun checkLoginSession(): String {
        return getLoginSession()
    }

    suspend fun deleteFragmentSession() {
        val session = getFragmentSession()
        try {
            apiService.deleteSession(sessionId = Session(session_id = session))
            editor.remove(FRAGMENTS_KEY).commit()
            deleteCurrentUserId()
        } catch (e: Exception) {
            editor.remove(FRAGMENTS_KEY).commit()
            deleteCurrentUserId()
        }
    }

    fun deleteLoginSession() {
        try {
            editor.remove(LOGIN_KEY).commit()
        } catch (e: Exception) {
        }
    }

    private fun deleteCurrentUserId() {
        try {
            editor.remove(CURRENT_USER_ID).commit()
        } catch (e: Exception) {
        }
    }

    suspend fun getFavorites(page: Int): List<Movie> {
        var movie = listOf<Movie>()
        val session = getFragmentSession()
        try {
            val response = apiService.getFavorites(session_id = session, page = page)
            if (response.isSuccessful) {
                movie = response.body()?.movies as List<Movie>
            }
        } catch (e: Exception) {
        }
        return movie
    }

    suspend fun addOrDeleteFavorite(movieId: Int, favoriteState: Boolean): LoadingState {
        var loadingState = LoadingState.FINISHED
        val session = getFragmentSession()
        val postMovie = PostMovie(media_id = movieId, isFavorite = favoriteState)
        try {
            val response = apiService.addFavorite(
                session_id = session,
                postMovie = postMovie
            )
            if (response.isSuccessful) {
                updateMovie(postMovie.media_id, postMovie.isFavorite)
                loadingState = LoadingState.SUCCESS
            }
        } catch (e: Exception) {
        }
        return loadingState
    }

    suspend fun getAccountState(movie: Movie): Movie {
        val session = getFragmentSession()
        try {
            val response =
                apiService.getAccountStates(id = movie.id as Int, session_id = session)
            if (response.isSuccessful) {
                movie.isFavorite = response.body()?.favorite as Boolean
            }
        } catch (e: Exception) {
        }
        return movie
    }

    suspend fun addUser() {
        val session = getFragmentSession()
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
                    if (user.username != "User Name") {
                        db.insertUser(user)
                        editor.putInt(CURRENT_USER_ID, result.id as Int).commit()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    suspend fun getUser(): DbAccountDetails {
        val userId = getCurrentUserId()
        return db.getUserById(userId)
    }

    companion object {

        const val APP_SETTINGS = "Settings"
        const val FRAGMENTS_KEY = "SESSION_FRAGMENT"
        const val LOGIN_KEY = "SESSION_LOGIN"
        const val CURRENT_USER_ID = "CURRENT_USER"
    }
}