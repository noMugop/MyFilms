package com.example.myfilms.presentation.fragments.movies

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.Session
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.Utils.LoadingState
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

    fun loadData(page: Int) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.IS_LOADING
            withContext(Dispatchers.IO) {
                _loadingState.postValue(repository.loadData(page))
            }
        }
    }

    fun getMoviesList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _movies.postValue(repository.getMovieList())
            }
            if (!movies.value.isNullOrEmpty()) {
                _loadingState.value = LoadingState.SUCCESS
            }
        }
    }

    fun deleteSession() {
        viewModelScope.launch {
            repository.deleteSession()
        }
    }
}