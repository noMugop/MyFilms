package com.example.myfilms.presentation.fragments.login

import androidx.lifecycle.*
import com.example.myfilms.domain.repository.MovieRepository
import com.example.myfilms.domain.usecase.*
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val getFavoritesFromNetworkUseCase: GetFavoritesFromNetworkUseCase,
    private val getMainSessionUseCase: GetMainSessionUseCase,
    private val getLoginSessionUseCase: GetLoginSessionUseCase,
    private val deleteLoginSessionUseCase: DeleteLoginSessionUseCase
) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    fun checkSessionId() = getMainSessionUseCase()

    fun setSuccess() {
        if (getLoginSessionUseCase() == "Access") {
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
            val session = loginUseCase(username, password)
            if (session.isNotBlank()) {
                addUserUseCase()
                _loadingState.value = LoadingState.FINISHED
                _loadingState.value = LoadingState.SUCCESS
            } else {
                _loadingState.value = LoadingState.WAIT
            }
        }
    }

    fun getFavorites() {
        viewModelScope.launch(Dispatchers.Default) {
            getFavoritesFromNetworkUseCase()
        }
    }

    fun deleteLoginSession() {
        deleteLoginSessionUseCase()
    }
}