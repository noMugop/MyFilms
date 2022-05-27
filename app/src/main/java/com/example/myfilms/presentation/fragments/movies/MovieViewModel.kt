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

    private val dataUpdater = MutableLiveData("")

    private var _moviesFlow: Flow<PagingData<MovieDbModel>>
    val moviesFlow: Flow<PagingData<MovieDbModel>>
        get() = _moviesFlow

    init {
        _moviesFlow = getMoviesFromNetwork()
    }

    private fun getMoviesFromNetwork(): Flow<PagingData<MovieDbModel>> {
        return dataUpdater.asFlow()
            .flatMapLatest {
                getMoviesFromNetworkUseCase()
            }
            .cachedIn(viewModelScope)
    }

    fun onRefresh() {
        this.dataUpdater.postValue(this.dataUpdater.value)
    }

    fun deleteMainSession() {
        viewModelScope.launch {
            deleteMainSessionUseCase()
        }
    }
}