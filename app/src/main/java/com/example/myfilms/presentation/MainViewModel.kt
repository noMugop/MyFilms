package com.example.myfilms.presentation

import androidx.lifecycle.*
import com.example.myfilms.data.models.account.DbAccountDetails
import com.example.myfilms.data.repository.MovieRepositoryImpl
import com.example.myfilms.domain.MovieRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _user = MutableLiveData<DbAccountDetails?>()
    val user: LiveData<DbAccountDetails?>
        get() = _user

    fun getSession(): String {
        return movieRepository.getMainSession()
    }

    fun deleteMainSession() {
        viewModelScope.launch {
            movieRepository.deleteMainSession()
        }
    }

    fun cleanUser() {
        _user.value = null
    }

    fun deleteFavoriteMovies() {
        viewModelScope.launch {
            movieRepository.deleteFavoriteMovies()
        }
    }

    fun getUser() {
        viewModelScope.launch {
            val user = movieRepository.getUser()
            try {
                if (user.id != null) {
                    _user.value = user
                }
            } catch (e: Exception) {
            }
        }
    }
}