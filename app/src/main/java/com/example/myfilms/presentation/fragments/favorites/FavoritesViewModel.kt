package com.example.myfilms.presentation.fragments.favorites

import androidx.lifecycle.*
import androidx.paging.PagingData
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.domain.repository.MovieRepository
import com.example.myfilms.domain.usecase.DeleteMainSessionUseCase
import com.example.myfilms.domain.usecase.GetFavoritesFromDbUseCase
import com.example.myfilms.domain.usecase.GetMainSessionUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoritesFromDbUseCase: GetFavoritesFromDbUseCase,
    private val getMainSessionUseCase: GetMainSessionUseCase,
    private val deleteMainSessionUseCase: DeleteMainSessionUseCase
) : ViewModel() {

    val favoritesFlow: Flow<PagingData<MovieDbModel>> = getFavoritesFromDbUseCase("")

    fun checkSession() = getMainSessionUseCase()

    fun deleteMainSession() {
        viewModelScope.launch {
            deleteMainSessionUseCase()
        }
    }
}