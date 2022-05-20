package com.example.myfilms.presentation.fragments.favorites

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.fragments.login.LoginViewModel
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    val favoritesFlow: Flow<PagingData<Movie>> = repository.getFavoriteMovies()

    init {
        checkSession()
    }

    private fun checkSession() {
        if (repository.getMainSession().isBlank()) {
            Toast.makeText(
                context,
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