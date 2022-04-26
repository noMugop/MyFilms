package com.example.myfilms.presentation.fragments.movies

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.Session
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.launch
import java.lang.Exception

class ViewModelMovie(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    fun loadData(page: Int) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.IS_LOADING
            _loadingState.value = repository.loadData(page)
        }
    }

    fun getMoviesList(): LiveData<List<Movie>> {

        _loadingState.value = LoadingState.IS_LOADING
        val result = repository.getMovieList()
        if (result.value != null) {
            _loadingState.value = LoadingState.FINISHED
            _loadingState.value = LoadingState.SUCCESS
        } else {
            println("RESULT IS: ${result.value}")
        }

        return result
    }

    fun deleteSession() {
        viewModelScope.launch {
            repository.deleteSession()
        }
    }
}