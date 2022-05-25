package com.example.myfilms.presentation.fragments.movies

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.domain.repository.MovieRepository
import com.example.myfilms.domain.usecase.DeleteMainSessionUseCase
import com.example.myfilms.domain.usecase.GetMoviesFromNetworkUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieViewModel(
    private val getMoviesFromNetworkUseCase: GetMoviesFromNetworkUseCase,
    private val deleteMainSessionUseCase: DeleteMainSessionUseCase
) : ViewModel() {

    val moviesFlow: Flow<PagingData<MovieDbModel>> =
        getMoviesFromNetworkUseCase().cachedIn(viewModelScope)

    fun deleteMainSession() {
        viewModelScope.launch {
            deleteMainSessionUseCase()
        }
    }
}