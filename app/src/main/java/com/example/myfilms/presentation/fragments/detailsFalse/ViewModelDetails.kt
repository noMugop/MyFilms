package com.example.myfilms.presentation.fragments.detailsFalse

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.MovieVideos
import com.example.myfilms.data.models.PostMovie
import com.example.myfilms.data.models.ResultVideos
import com.example.myfilms.presentation.Utils.LoadingState
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class ViewModelDetails : ViewModel() {

    private val apiService = ApiFactory.getInstance()

    private val _movie = MutableLiveData<Movie>()
    val movie: LiveData<Movie>
        get() = _movie

    private val _videos = MutableLiveData<MovieVideos>()
    val videos: LiveData<MovieVideos>
        get() = _videos

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _addFavoriteState = MutableLiveData<LoadingState>()
    val addFavoriteState: LiveData<LoadingState>
        get() = _addFavoriteState

    fun getMovieById(movieId: Int) {

        viewModelScope.launch {
            _loadingState.value = LoadingState.IS_LOADING
            val responseMovie = apiService.getById(movieId)
            if (responseMovie.isSuccessful) {
                _movie.value = responseMovie.body()
            }
            val responseVideo = apiService.getVideos(movieId)
            if (responseVideo.isSuccessful) {
                _videos.value = responseVideo.body()
            }
            _loadingState.value = LoadingState.FINISHED
            _loadingState.value = LoadingState.SUCCESS
        }
    }

    fun deleteFavorites(movieId: Int, sessionId: String) {

        viewModelScope.launch {
            val postMovie = PostMovie(media_id = movieId, favorite = false)
            val response = apiService.addFavorite(
                session_id = sessionId,
                postMovie = postMovie
            )
            if (response.isSuccessful) {
                _addFavoriteState.value = LoadingState.SUCCESS
                _addFavoriteState.value = LoadingState.IS_LOADING
            } else {
                _addFavoriteState.value = LoadingState.FINISHED
                _addFavoriteState.value = LoadingState.IS_LOADING

            }
        }
    }

    fun addFavorite(movieId: Int, sessionId: String) {
        viewModelScope.launch {
            val postMovie = PostMovie(media_id = movieId, favorite = true)
            val response = apiService.addFavorite(
                session_id = sessionId,
                postMovie = postMovie
            )
            if (response.isSuccessful) {
                _addFavoriteState.value = LoadingState.SUCCESS
                _addFavoriteState.value = LoadingState.IS_LOADING
            } else {
                _addFavoriteState.value = LoadingState.FINISHED
                _addFavoriteState.value = LoadingState.IS_LOADING
            }
        }
    }
}