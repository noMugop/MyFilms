package com.example.myfilms.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.aware.DiscoverySession
import com.example.myfilms.data.database.MovieDatabase
import com.example.myfilms.data.models.*
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

    init {
        getSessionId()
    }

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

    suspend fun loadData(page: Int) {
        withContext(Dispatchers.Default) {
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
                    val session = apiService.createSession(token = responseApprove.body() as Token)
                    if (session.isSuccessful) {
                        SESSION_ID = session.body()?.session_id as String
                        editor.putString(SESSION_ID_KEY, SESSION_ID)
                        editor.commit()
                    }
                }
            }
        } catch (e: Exception) {
        }
        return SESSION_ID
    }

    private fun getSessionId() {
        try {
            SESSION_ID = prefSettings.getString(SESSION_ID_KEY, "") as String
        } catch (e: Exception) {
        }
    }

    fun checkSessionId(): String {
        getSessionId()
        return SESSION_ID
    }

    suspend fun deleteSession() {
        getSessionId()
        try {
            apiService.deleteSession(sessionId = Session(session_id = SESSION_ID))
            editor.clear().commit()
        } catch (e: Exception) {
            editor.clear().commit()
        }
    }

    suspend fun getFavorites(page: Int): List<Movie> {
        var movie = listOf<Movie>()
        try {
            val response = apiService.getFavorites(session_id = SESSION_ID, page = page)
            if (response.isSuccessful) {
                movie = response.body()?.movies as List<Movie>
            }
        } catch (e: Exception) {
        }
        return movie
    }

    suspend fun addOrDeleteFavorite(movieId: Int, favoriteState: Boolean): LoadingState {
        var loadingState: LoadingState? = null
        val postMovie = PostMovie(media_id = movieId, isFavorite = favoriteState)
        try {
            val response = apiService.addFavorite(
                session_id = SESSION_ID,
                postMovie = postMovie
            )
            updateMovie(postMovie.media_id, postMovie.isFavorite)
            loadingState = if (response.isSuccessful) {
                LoadingState.SUCCESS
            } else {
                LoadingState.FINISHED
            }
        } catch (e: Exception) {
        }
        return loadingState as LoadingState
    }

    suspend fun getAccountState(movie: Movie): Movie {
        try {
            val response =
                apiService.getAccountStates(id = movie.id as Int, session_id = SESSION_ID)
            if (response.isSuccessful) {
                movie.isFavorite = response.body()?.favorite as Boolean
            }
        } catch (e: Exception) {
        }
        return movie
    }

    companion object {

        private var SESSION_ID = ""
        const val APP_SETTINGS = "Settings"
        const val SESSION_ID_KEY = "SESSION_ID"
    }
}