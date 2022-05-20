package com.example.myfilms.presentation.fragments.favorites

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    val favoritesFlow: Flow<PagingData<Movie>> =
        repository.getFavoriteMovies().cachedIn(viewModelScope)

    init {
        checkSession()
    }

    private fun checkSession() {
        if (repository.getFragmentSession().isBlank()) {
            Toast.makeText(
                context,
                "Требуется авторизация",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteFragmentSession()
            repository.deleteFavoriteMovies()
        }
    }
}