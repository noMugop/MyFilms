package com.example.myfilms.presentation

import androidx.lifecycle.*
import com.example.myfilms.data.database.model.user.AccountDetailsDbModel
import com.example.myfilms.domain.repository.MovieRepository
import com.example.myfilms.domain.usecase.*
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getMainSessionUseCase: GetMainSessionUseCase,
    private val deleteMainSessionUseCase: DeleteMainSessionUseCase,
    private val deleteFavoriteMoviesUseCase: DeleteFavoriteMoviesUseCase
) : ViewModel() {

    private val _user = MutableLiveData<AccountDetailsDbModel?>()
    val user: LiveData<AccountDetailsDbModel?>
        get() = _user

    fun getSession() = getMainSessionUseCase()

    fun deleteMainSession() {
        viewModelScope.launch {
            deleteMainSessionUseCase()
        }
    }

    fun cleanUser() {
        _user.value = null
    }

    fun deleteFavoriteMovies() {
        viewModelScope.launch {
            deleteFavoriteMoviesUseCase()
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
}