package com.example.myfilms.presentation.fragments.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myfilms.data.models.account.DbAccountDetails
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.presentation.Utils.LoadingState
import kotlinx.coroutines.launch
import java.lang.Exception

class ViewModelSettings(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    private val _user = MutableLiveData<DbAccountDetails?>()
    val user: LiveData<DbAccountDetails?>
        get() = _user

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

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

    fun updateUri(uri: String) {
        _user.value?.avatar_uri = uri
    }

    fun updateName(name: String) {
        _user.value?.name = name
    }

    fun isLoadingState() {
        _loadingState.value = LoadingState.IS_LOADING
    }

    fun waitState() {
        _loadingState.value = LoadingState.WAIT
    }

    fun updateUser() {
        viewModelScope.launch {
            if (_user.value?.id != null) {
                _loadingState.value = repository.updateAccount(
                    _user.value?.id as Int,
                    _user.value?.name as String,
                    _user.value?.avatar_uri as String)
            } else {
                _loadingState.value = LoadingState.FINISHED
            }
        }
    }
}