package com.example.myfilms.presentation.fragments.favorites

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.repository.MovieRepositoryImpl
import com.example.myfilms.domain.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    val favoritesFlow: Flow<PagingData<Movie>> = movieRepository.getFavoritesFromDB("")

    fun checkSession(): String {
        return movieRepository.getMainSession()
    }

    fun deleteMainSession() {
        viewModelScope.launch {
            movieRepository.deleteMainSession()
        }
    }
}