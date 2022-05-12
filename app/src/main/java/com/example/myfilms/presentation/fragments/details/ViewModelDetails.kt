package com.example.myfilms.presentation.fragments.details

import android.app.Application
import androidx.lifecycle.*
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.models.movie.MovieVideos
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.launch

class ViewModelDetails(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    private val _movie = MutableLiveData<Movie>()
    val movie: LiveData<Movie>
        get() = _movie

    private val _trailer = MutableLiveData<MovieVideos>()
    val trailer: LiveData<MovieVideos>
        get() = _trailer

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _addFavoriteState = MutableLiveData<LoadingState>()
    val addFavoriteState: LiveData<LoadingState>
        get() = _addFavoriteState

    fun getMovieById(movieId: Int) {
        viewModelScope.launch {
            _movie.value = repository.getMovieById(movieId)
            if (_movie.value != null) {
                val result = repository.getAccountState(_movie.value as Movie)
                repository.updateMovie(result.id as Int, result.isFavorite)
            }
            _trailer.value = repository.getTrailer(movieId)
            _loadingState.value = LoadingState.SUCCESS
        }
    }

    fun deleteFavorites(movieId: Int) {
        viewModelScope.launch {
            _addFavoriteState.value = repository.addOrDeleteFavorite(movieId, false)
            _addFavoriteState.value = LoadingState.IS_LOADING
        }
    }

    fun addFavorite(movieId: Int) {
        viewModelScope.launch {
            _addFavoriteState.value = repository.addOrDeleteFavorite(movieId, true)
            _addFavoriteState.value = LoadingState.IS_LOADING
        }
    }
}