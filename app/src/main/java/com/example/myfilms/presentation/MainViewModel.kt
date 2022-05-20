package com.example.myfilms.presentation

import android.app.Application
import androidx.lifecycle.*
import com.example.myfilms.data.models.account.DbAccountDetails
import com.example.myfilms.data.repository.Repository
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    private val _user = MutableLiveData<DbAccountDetails?>()
    val user: LiveData<DbAccountDetails?>
        get() = _user

    fun getSession(): String {
        return repository.getMainSession()
    }

    fun deleteMainSession() {
        viewModelScope.launch {
            repository.deleteMainSession()
        }
    }

    fun cleanUser() {
        _user.value = null
    }

    fun deleteFavoriteMovies() {
        viewModelScope.launch {
            repository.deleteFavoriteMovies()
        }
    }

    fun getUser() {
        viewModelScope.launch {
            val user = repository.getUser()
            try {
                if (user.id != null) {
                    _user.value = user
                }
            } catch (e: Exception) {
            }
        }
    }
}