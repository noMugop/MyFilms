package com.example.myfilms.presentation.fragments.movies

import android.app.Application
import androidx.lifecycle.*
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.launch

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
            _movies.value = repository.getMovieList()
            if (!movies.value.isNullOrEmpty()) {
                _loadingState.value = LoadingState.SUCCESS
            } else {
                _loadingState.value = LoadingState.FINISHED
            }
        }
    }

    fun syncFavorites(page: Int) {
        viewModelScope.launch {
            repository.syncFavorites(page)
        }
    }

    fun deleteSession() {
        viewModelScope.launch {
            repository.deleteFragmentSession()
        }
    }
}