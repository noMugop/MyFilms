package com.example.myfilms.presentation.fragments.movies

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    val moviesFlow: Flow<PagingData<Movie>> =
        repository.getMoviesFromNetwork().cachedIn(viewModelScope)

    fun deleteSession() {
        viewModelScope.launch {
            repository.deleteFragmentSession()
        }
    }
}