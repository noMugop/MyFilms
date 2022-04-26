package com.example.myfilms.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.myfilms.data.database.MovieDatabase
import com.example.myfilms.data.models.LoginApprove
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.Session
import com.example.myfilms.data.models.Token
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.presentation.Utils.LoadingState
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

    fun getMovieById() {

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

    fun getTrailer() {

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

    fun addFavorite() {

    }

    fun deleteFavorite() {

    }

    companion object {

        private var SESSION_ID = ""
        const val APP_SETTINGS = "Settings"
        const val SESSION_ID_KEY = "SESSION_ID"
    }
}