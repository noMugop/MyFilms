package com.example.myfilms.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.myfilms.data.database.MovieDatabase
import com.example.myfilms.data.models.*
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.launch
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
        try {
            SESSION_ID =
                prefSettings.getString(SESSION_ID_KEY, null) as String
        } catch (e: Exception) {
        }
    }

    suspend fun getMovieList(): List<Movie> {
        return db.getMovieList()
    }

    suspend fun getMovieById(movieId: Int): Movie {
        return db.getMovieById(movieId)
    }

    suspend fun loadData(page: Int): LoadingState {
        var loadingState: LoadingState? = null
        val response = apiService.getMovies(page = page)
        if (response.isSuccessful) {
            val result = response.body()?.movies
            if (!result.isNullOrEmpty()) {
                db.loadData(result)
                loadingState = LoadingState.FINISHED
            }
        }
        return loadingState as LoadingState
    }

    suspend fun getTrailer(movieId: Int): MovieVideos {
        var result = MovieVideos()
        val responseVideo = apiService.getTrailer(movieId)
        if (responseVideo.isSuccessful) {
            result = responseVideo.body() as MovieVideos
        }
        return result
    }

    suspend fun login(data: LoginApprove): String {

        var sessionId = String()
        val responseGet = apiService.getToken()
        if (responseGet.isSuccessful) {
            val loginApprove = LoginApprove(
                username = data.username,
                password = data.password,
                request_token = responseGet.body()?.request_token as String
            )
            val responseApprove = apiService.approveToken(loginApprove = loginApprove)
            if (responseApprove.isSuccessful) {
                val session = apiService.createSession(token = responseApprove.body() as Token)
                if (session.isSuccessful) {
                    sessionId = session.body()?.session_id as String
                    editor.putString(SESSION_ID_KEY, sessionId)
                    editor.commit()
                }
            }
        }
        return sessionId
    }

    suspend fun deleteSession() {
        try {
            apiService.deleteSession(sessionId = Session(session_id = SESSION_ID))
            editor.clear().commit()
        } catch (e: Exception) {
        }
    }

    suspend fun getFavorites(page: Int): List<Movie> {

        var movie = listOf<Movie>()
        val response = apiService.getFavorites(session_id = SESSION_ID, page = page)
        if (response.isSuccessful) {
            movie = response.body()?.movies as List<Movie>
        }
        return movie
    }

    suspend fun addFavorite(postMovie: PostMovie): LoadingState {
        val response = apiService.addFavorite(
            session_id = SESSION_ID,
            postMovie = postMovie
        )
        val loadingState = if (response.isSuccessful) {
            LoadingState.SUCCESS
        } else {
            LoadingState.FINISHED
        }
        return loadingState
    }

    companion object {

        private var SESSION_ID = ""
        const val APP_SETTINGS = "Settings"
        const val SESSION_ID_KEY = "SESSION_ID"
    }
}