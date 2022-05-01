package com.example.myfilms.presentation.fragments.login

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.myfilms.data.models.*
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.launch

class ViewModelLogin(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _sessionId = MutableLiveData<String>()
    val sessionId: LiveData<String>
        get() = _sessionId

    fun login(data: LoginApprove) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.IS_LOADING
            val session = repository.login(data)
            if (session.isNotBlank()) {
                _sessionId.value = session
                _loadingState.value = LoadingState.FINISHED
            } else {
                _loadingState.value = LoadingState.FINISHED
                Toast.makeText(context, "Неверные данные", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadData(page: Int) {
        viewModelScope.launch {
            repository.loadData(page)
            _loadingState.value = LoadingState.SUCCESS
        }
    }

    fun deleteSession() {
        viewModelScope.launch {
            repository.deleteSession()
        }
    }
}