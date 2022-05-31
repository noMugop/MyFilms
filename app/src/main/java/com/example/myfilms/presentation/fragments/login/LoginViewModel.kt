package com.example.myfilms.presentation.fragments.login

import androidx.lifecycle.*
import com.example.myfilms.domain.usecase.*
import com.example.myfilms.presentation.utils.ExceptionStatus
import com.example.myfilms.presentation.utils.LoadingState
import com.example.myfilms.domain.utils.getErrorMessage
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
        if (getLoginSessionUseCase() == ACCESS) {
            _loadingState.value = LoadingState.SUCCESS
        } else {
            _loadingState.value = LoadingState.WARNING
        }
    }

    fun setWarning() {
        _loadingState.value = LoadingState.WARNING
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING
            val resultCode = loginUseCase(username, password)
            if (resultCode < ExceptionStatus.SUCCESS_CODE.code &&
                resultCode != ExceptionStatus.UNKNOWN_EXCEPTION.code
            ) {
                addUserUseCase()
                _loadingState.value = LoadingState.DONE
                _loadingState.value = LoadingState.SUCCESS
            } else {
                errorMsg = getErrorMessage(resultCode)
                _loadingState.value = LoadingState.WARNING
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

    companion object {

        var errorMsg = ""
        private const val ACCESS = "Access"
    }
}