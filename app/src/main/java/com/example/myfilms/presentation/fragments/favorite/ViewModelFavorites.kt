package com.example.myfilms.presentation.fragments.favorite

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.Session
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.launch

class ViewModelFavorites(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    fun getFavorites(page: Int) {

        viewModelScope.launch {
            _loadingState.value = LoadingState.IS_LOADING
            val result = repository.getFavorites(page)

            if (!result.isNullOrEmpty()) {
                _movies.value = repository.getFavorites(page)
                _loadingState.value = LoadingState.FINISHED
                _loadingState.value = LoadingState.SUCCESS
            } else {
                Toast.makeText(
                    context,
                    "Требуется авторизация",
                    Toast.LENGTH_SHORT
                ).show()
                _loadingState.value = LoadingState.FINISHED
                _loadingState.value = LoadingState.SUCCESS
            }
        }
    }

    fun deleteSession() {
        viewModelScope.launch {
            repository.deleteSession()
        }
    }
}