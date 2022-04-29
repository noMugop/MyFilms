package com.example.myfilms.presentation.fragments.details

import android.app.Application
import androidx.lifecycle.*
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.MovieVideos
import com.example.myfilms.data.models.PostMovie
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                repository.getAccountState(_movie.value as Movie)
            }
            _trailer.value = repository.getTrailer(movieId)
            _loadingState.value = LoadingState.SUCCESS
        }
    }

    fun deleteFavorites(movieId: Int) {
        viewModelScope.launch {
            val postMovie = PostMovie(media_id = movieId, isFavorite = false)
            _addFavoriteState.value = repository.addOrDeleteFavorite(postMovie)
            _addFavoriteState.value = LoadingState.IS_LOADING
        }
    }

    fun addFavorite(movieId: Int) {
        viewModelScope.launch {
            val postMovie = PostMovie(media_id = movieId, isFavorite = true)
            _addFavoriteState.value = repository.addOrDeleteFavorite(postMovie)
            _addFavoriteState.value = LoadingState.IS_LOADING
        }
    }
}