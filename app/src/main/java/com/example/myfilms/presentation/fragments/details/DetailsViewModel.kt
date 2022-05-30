package com.example.myfilms.presentation.fragments.details

import androidx.lifecycle.*
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.data.network.model.movie.MovieTrailerDto
import com.example.myfilms.domain.usecase.AddOrDeleteFavoriteUseCase
import com.example.myfilms.domain.usecase.GetFavoriteMovieByIdUseCase
import com.example.myfilms.domain.usecase.GetTrailerUseCase
import com.example.myfilms.utils.LoadingState
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getFavoriteMovieByIdUseCase: GetFavoriteMovieByIdUseCase,
    private val getTrailerUseCase: GetTrailerUseCase,
    private val addOrDeleteFavoriteUseCase: AddOrDeleteFavoriteUseCase
) : ViewModel() {

    private val _movie = MutableLiveData<MovieDbModel>()
    val movieDbModel: LiveData<MovieDbModel>
        get() = _movie

    private val _trailer = MutableLiveData<MovieTrailerDto>()
    val trailer: LiveData<MovieTrailerDto>
        get() = _trailer

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _addFavoriteState = MutableLiveData<LoadingState>()
    val addFavoriteState: LiveData<LoadingState>
        get() = _addFavoriteState

    fun getMovieById(movieId: Int) {
        viewModelScope.launch {
            _movie.value = getFavoriteMovieByIdUseCase.invoke(movieId)
            _trailer.value = getTrailerUseCase.invoke(movieId)
            _loadingState.value = LoadingState.SUCCESS
        }
    }

    fun deleteFavorites(movieDbModel: MovieDbModel) {
        viewModelScope.launch {
            movieDbModel.isFavorite = false
            _addFavoriteState.value = addOrDeleteFavoriteUseCase.invoke(movieDbModel)
            _addFavoriteState.value = LoadingState.LOADING
        }
    }

    fun addFavorite(movieDbModel: MovieDbModel) {
        viewModelScope.launch {
            movieDbModel.isFavorite = true
            _addFavoriteState.value = addOrDeleteFavoriteUseCase.invoke(movieDbModel)
            _addFavoriteState.value = LoadingState.LOADING
        }
    }
}