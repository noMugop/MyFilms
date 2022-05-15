package com.example.myfilms.presentation.fragments.login

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.launch

class ViewModelLogin(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

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
            } else {
                _loadingState.value = LoadingState.WAIT
                Toast.makeText(context, "Неверные данные", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadData(page: Int) {
        viewModelScope.launch {
            _loadingState.value = repository.loadData(page)
        }
    }

    fun deleteFragmentSession() {
        viewModelScope.launch {
            repository.deleteFragmentSession()
        }
    }

    fun deleteLoginSession() {
        repository.deleteLoginSession()
    }
}