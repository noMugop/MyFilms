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
        return repository.getFragmentSession()
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteMainSession()
            repository.deleteFavoriteMovies()
        }
    }

    fun cleanUser() {
        _user.value = DbAccountDetails()
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