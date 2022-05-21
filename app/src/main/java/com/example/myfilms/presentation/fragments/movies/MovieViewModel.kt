package com.example.myfilms.presentation.fragments.movies

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.repository.RepositoryImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieViewModel(
    val repository: RepositoryImpl
) : ViewModel() {

    val moviesFlow: Flow<PagingData<Movie>> =
        repository.getMoviesFromNetwork().cachedIn(viewModelScope)

    fun deleteMainSession() {
        viewModelScope.launch {
            repository.deleteMainSession()
        }
    }
}