package com.example.myfilms.presentation

import androidx.lifecycle.*
import com.example.myfilms.data.database.model.user.AccountDetailsDbModel
import com.example.myfilms.domain.repository.MovieRepository
import com.example.myfilms.domain.usecase.*
import com.example.myfilms.presentation.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getMainSessionUseCase: GetMainSessionUseCase,
    private val deleteMainSessionUseCase: DeleteMainSessionUseCase,
    private val getFavoritesFromNetworkUseCase: GetFavoritesFromNetworkUseCase
) : ViewModel() {

    private val _user = MutableLiveData<AccountDetailsDbModel?>()
    val user: LiveData<AccountDetailsDbModel?>
        get() = _user

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    fun getSession() = getMainSessionUseCase()

    fun deleteMainSession() {
        viewModelScope.launch {
            deleteMainSessionUseCase()
        }
    }

    fun cleanUser() {
        _user.value = null
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

    fun setLoading() {
        _loadingState.value = LoadingState.LOADING
    }

    fun getFavorites() {
        viewModelScope.launch(Dispatchers.Default) {
            if (getFavoritesFromNetworkUseCase.invoke().isNotBlank()) {
                _loadingState.postValue(LoadingState.SUCCESS)
            } else {
                _loadingState.postValue(LoadingState.WARNING)
            }
        }
    }
}