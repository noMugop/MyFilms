package com.example.myfilms.presentation.fragments.login

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _loadFavorites = MutableLiveData<LoadingState>()
    val loadFavorites: LiveData<LoadingState>
        get() = _loadFavorites

    fun checkSessionId(): String {
        return repository.getFragmentSession()
    }

    fun setSuccess() {
        if (repository.getLoginSession() == "Access") {
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
            val session = repository.login(username, password)
            if (session.isNotBlank()) {
                repository.addUser()
                _loadingState.value = LoadingState.FINISHED
                _loadingState.value = LoadingState.SUCCESS
            } else {
                _loadingState.value = LoadingState.WAIT
                Toast.makeText(context, "Неверные данные", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getFavorites() {
        viewModelScope.launch {
            _loadFavorites.postValue(LoadingState.IS_LOADING)
            while (_loadFavorites.value != LoadingState.SUCCESS) {
                _loadFavorites.postValue(repository.getFavorites(PAGE))
                PAGE++
            }
        }
    }

    fun deleteMainSession() {
        viewModelScope.launch {
            repository.deleteMainSession()
        }
    }

    fun deleteLoginSession() {
        repository.deleteLoginSession()
    }

    companion object {

        private var PAGE = 1
    }
}