package com.example.myfilms.presentation.fragments.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.domain.usecase.DeleteFavoriteMoviesUseCase
import com.example.myfilms.domain.usecase.GetMainSessionUseCase
import com.example.myfilms.domain.usecase.SearchMoviesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

class SearchViewModel(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private var _moviesFlow: Flow<PagingData<MovieDbModel>>
    val moviesFlow: Flow<PagingData<MovieDbModel>>
        get() = _moviesFlow

    private val searchBy = MutableLiveData("")

    init {
        _moviesFlow = searchBy.asFlow()
            .debounce(500)
            .flatMapLatest {
                searchMoviesUseCase(it)
            }
            .cachedIn(viewModelScope)
    }

    fun setSearchBy(value: String) {
        if (this.searchBy.value == value) return
        this.searchBy.value = value
    }

    fun refresh() {
        this.searchBy.postValue(this.searchBy.value)
    }
}