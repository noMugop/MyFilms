package com.example.myfilms.presentation.fragments.favorites

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.repository.RepositoryImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: RepositoryImpl,
    val application: Application
) : ViewModel() {

    val favoritesFlow: Flow<PagingData<Movie>> = repository.getFavoriteMovies()

    init {
        checkSession()
    }

    private fun checkSession() {
        if (repository.getMainSession().isBlank()) {
            Toast.makeText(
                application,
                "Требуется авторизация",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun deleteMainSession() {
        viewModelScope.launch {
            repository.deleteMainSession()
        }
    }
}