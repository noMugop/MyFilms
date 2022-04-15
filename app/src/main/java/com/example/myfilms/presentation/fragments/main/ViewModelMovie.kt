package com.example.myfilms.presentation.fragments.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.Session
import com.example.myfilms.presentation.Utils.LoadingState
import com.example.myfilms.presentation.fragments.login.LoginFragment
import kotlinx.coroutines.launch
import java.lang.Exception

class ViewModelMovie : ViewModel() {

    private val apiService = ApiFactory.getInstance()

    private var oldList = mutableListOf<Movie>()
    private var newList = mutableListOf<Movie>()

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    fun downloadData(page: Int) {

        viewModelScope.launch {

            _loadingState.value = LoadingState.IS_LOADING
            val response = apiService.getMovies(page = page)
            if (response.isSuccessful) {
                _movies.value = response.body()?.movies as List<Movie>
                _loadingState.value = LoadingState.FINISHED
                _loadingState.value = LoadingState.SUCCESS
            }
        }
    }

    fun deleteSession(session: String) {
        viewModelScope.launch {
            apiService.deleteSession(sessionId = Session(session_id = session))
        }
    }
}