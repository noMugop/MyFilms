package com.example.myfilms.presentation.fragments.movies

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.MovieUpdate
import com.example.myfilms.data.models.Session
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.Utils.LoadingState
import com.example.myfilms.presentation.fragments.login.ViewModelLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ViewModelMovie(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies

    fun getMoviesList() {
        viewModelScope.launch {
            _loadingState.value = LoadingState.IS_LOADING
            withContext(Dispatchers.IO) {
                _movies.postValue(repository.getMovieList())
            }
            if (!movies.value.isNullOrEmpty()) {
                _loadingState.value = LoadingState.FINISHED
            } else {
                _loadingState.value = LoadingState.SUCCESS
            }
        }
    }

    fun synchronizeFavorites() {
        if (!SYNCHRONIZED) {
            viewModelScope.launch {
                val result = repository.getFavorites(PAGE)
                if (!result.isNullOrEmpty()) {
                    PAGE++
                    withContext(Dispatchers.IO) {
                        result.map {
                            val localMovies = _movies.value as List<Movie>
                            for (movie in localMovies) {
                                if (movie.id == it.id) {
                                    val updateMovie =
                                        MovieUpdate(id = movie.id as Int, isFavorite = true)
                                    repository.updateMovie(updateMovie)
                                } else {
                                    it.isFavorite = true
                                    repository.insertMovie(it)
                                }
                            }
                        }
                        _loadingState.postValue(LoadingState.FINISHED)
                    }
                } else if (result.isNullOrEmpty() && LUST_PAGE == PAGE && PAGE != 1) {
                    PAGE = 1
                    SYNCHRONIZED = true
                    _loadingState.value = LoadingState.SUCCESS
                } else if (result.isNullOrEmpty() && LUST_PAGE == PAGE) {
                    _loadingState.value = LoadingState.SUCCESS
                } else {
                    LUST_PAGE = PAGE
                    _loadingState.value = LoadingState.FINISHED
                }
            }
        } else {
            _loadingState.value = LoadingState.SUCCESS
        }
    }

    fun deleteSession() {
        viewModelScope.launch {
            repository.deleteSession()
        }
    }

    companion object {

        private var PAGE = 1
        private var LUST_PAGE = 0
        private var SYNCHRONIZED = false
    }
}