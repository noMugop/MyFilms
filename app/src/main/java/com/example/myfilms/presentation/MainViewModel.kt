package com.example.myfilms.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.models.Session
import com.example.myfilms.data.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val repository = Repository(context)

    fun deleteSession() {
        viewModelScope.launch {
            repository.deleteSession()
        }
    }
}