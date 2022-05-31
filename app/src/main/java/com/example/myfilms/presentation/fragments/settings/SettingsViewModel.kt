package com.example.myfilms.presentation.fragments.settings

import androidx.lifecycle.*
import com.example.myfilms.data.database.model.user.AccountDetailsDbModel
import com.example.myfilms.domain.usecase.GetUserUseCase
import com.example.myfilms.domain.usecase.UpdateUserUseCase
import com.example.myfilms.presentation.utils.ExceptionStatus
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.launch
import java.lang.Exception

class SettingsViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _user = MutableLiveData<AccountDetailsDbModel>()
    val user: LiveData<AccountDetailsDbModel>
        get() = _user

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

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

    fun isLoadingState() {
        _loadingState.value = LoadingState.LOADING
    }

    fun doneState() {
        _loadingState.value = LoadingState.DONE
    }

    fun updateUser() {
        viewModelScope.launch {
            if (_user.value?.id != null) {
                val user = _user.value as AccountDetailsDbModel
                val resultCode = updateUserUseCase.invoke(user)
                if (resultCode < ExceptionStatus.SUCCESS_CODE.code &&
                    resultCode != ExceptionStatus.UNKNOWN_EXCEPTION.code
                ) {
                    _loadingState.value = LoadingState.SUCCESS
                } else {
                    _loadingState.value = LoadingState.WARNING
                }
            } else {
                _loadingState.value = LoadingState.WARNING
            }
        }
    }
}