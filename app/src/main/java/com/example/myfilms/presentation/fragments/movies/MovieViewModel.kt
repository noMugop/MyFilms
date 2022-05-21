package com.example.myfilms.presentation.fragments.movies

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.domain.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    val moviesFlow: Flow<PagingData<Movie>> =
        movieRepository.getMoviesFromNetwork().cachedIn(viewModelScope)

    fun deleteMainSession() {
        viewModelScope.launch {
            movieRepository.deleteMainSession()
        }
    }
}