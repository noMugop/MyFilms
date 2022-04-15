package com.example.myfilms.presentation.fragments.favorites

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.Session
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.launch
import java.lang.Exception

class ViewModelFavorites(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val apiService = ApiFactory.getInstance()

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>>
        get() = _movies

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    fun downloadData(session: String, page: Int) {

        //у ViewModel есть встроенные корутины, их не нужно создавать отдельно
        viewModelScope.launch {

            _loadingState.value = LoadingState.IS_LOADING
            val response = apiService.getFavorites(session_id = session, page = page)

            if (response.isSuccessful) {
                _movies.value = response.body()?.movies
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

    fun deleteSession(session: String) {
        viewModelScope.launch {
            apiService.deleteSession(sessionId = Session(session_id = session))
        }
    }
}