package com.example.myfilms.presentation.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfilms.data.database.model.user.AccountDetailsDbModel
import com.example.myfilms.domain.usecase.*
import com.example.myfilms.domain.utils.getErrorMessage
import com.example.myfilms.presentation.utils.ExceptionStatus
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val getMainSessionUseCase: GetMainSessionUseCase,
    private val deleteMainSessionUseCase: DeleteMainSessionUseCase,
    private val deleteFavoriteMoviesUseCase: DeleteFavoriteMoviesUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _user = MutableLiveData<AccountDetailsDbModel>()
    val user: LiveData<AccountDetailsDbModel>
        get() = _user

    private val _loginLoadingState = MutableLiveData<LoadingState>()
    val loginLoadingState: LiveData<LoadingState>
        get() = _loginLoadingState

    private val _settingsLoadingState = MutableLiveData<LoadingState>()
    val settingsLoadingState: LiveData<LoadingState>
        get() = _settingsLoadingState

    fun setSuccess() {
        if (getMainSessionUseCase.invoke().isNotBlank()) {
            _loginLoadingState.value = LoadingState.SUCCESS
        } else {
            _loginLoadingState.value = LoadingState.WARNING
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginLoadingState.value = LoadingState.LOADING
            val resultCode = loginUseCase(username, password)
            if (resultCode < ExceptionStatus.SUCCESS_CODE.code &&
                resultCode != ExceptionStatus.UNKNOWN_EXCEPTION.code
            ) {
                addUserUseCase()
                _loginLoadingState.value = LoadingState.SUCCESS
            } else {
                errorMsg = getErrorMessage(resultCode)
                _loginLoadingState.value = LoadingState.WARNING
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            val user = getUserUseCase()
            try {
                if (user.id != null) {
                    _user.value = user
                }
            } catch (e: Exception) {
            }
        }
    }

    fun updateUri(uri: String) {
        _user.value?.avatar_uri = uri
    }

    fun updateName(name: String) {
        _user.value?.name = name
    }

    fun setLoading() {
        _settingsLoadingState.value = LoadingState.LOADING
    }

    fun setDone() {
        _settingsLoadingState.value = LoadingState.DONE
    }

    fun updateUser() {
        viewModelScope.launch {
            if (_user.value?.id != null) {
                val user = _user.value as AccountDetailsDbModel
                val resultCode = updateUserUseCase.invoke(user)
                if (resultCode < ExceptionStatus.SUCCESS_CODE.code &&
                    resultCode != ExceptionStatus.UNKNOWN_EXCEPTION.code
                ) {
                    _settingsLoadingState.value = LoadingState.SUCCESS
                } else {
                    _settingsLoadingState.value = LoadingState.WARNING
                }
            } else {
                _settingsLoadingState.value = LoadingState.WARNING
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            deleteMainSessionUseCase()
            deleteFavoriteMoviesUseCase()
        }
    }

    companion object {

        var errorMsg = ""
    }
}