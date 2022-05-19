package com.example.myfilms.presentation.fragments.details

import android.app.Application
import androidx.lifecycle.*
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.models.movie.MovieVideos
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

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
            _trailer.value = repository.getTrailer(movieId)
            _loadingState.value = LoadingState.SUCCESS
        }
    }

    fun deleteFavorites(movie: Movie) {
        viewModelScope.launch {
            movie.isFavorite = false
            _addFavoriteState.value = repository.addOrDeleteFavorite(movie)
            _addFavoriteState.value = LoadingState.IS_LOADING
        }
    }

    fun addFavorite(movie: Movie) {
        viewModelScope.launch {
            movie.isFavorite = true
            _addFavoriteState.value = repository.addOrDeleteFavorite(movie)
            _addFavoriteState.value = LoadingState.IS_LOADING
        }
    }
}