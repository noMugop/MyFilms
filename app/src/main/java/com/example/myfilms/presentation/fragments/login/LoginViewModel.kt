package com.example.myfilms.presentation.fragments.login

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.data.repository.MovieRepositoryImpl
import com.example.myfilms.domain.MovieRepository
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val movieRepository: MovieRepository,
    private val application: Application
) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    fun checkSessionId(): String {
        return movieRepository.getMainSession()
    }

    fun setSuccess() {
        if (movieRepository.getLoginSession() == "Access") {
            _loadingState.value = LoadingState.SUCCESS
        } else {
            _loadingState.value = LoadingState.WAIT
        }
    }

    fun setWait() {
        _loadingState.value = LoadingState.WAIT
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.IS_LOADING
            val session = movieRepository.login(username, password)
            if (session.isNotBlank()) {
                movieRepository.addUser()
                _loadingState.value = LoadingState.FINISHED
                _loadingState.value = LoadingState.SUCCESS
            } else {
                _loadingState.value = LoadingState.WAIT
                Toast.makeText(application, "Неверные данные", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getFavorites() {
        viewModelScope.launch(Dispatchers.Default) {
            movieRepository.getFavoritesFromNetwork()
        }
    }

    fun deleteLoginSession() {
        movieRepository.deleteLoginSession()
    }
}