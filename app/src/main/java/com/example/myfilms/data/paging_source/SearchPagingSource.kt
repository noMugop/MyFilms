package com.example.myfilms.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception

class SearchPagingSource(
    private val apiService: ApiService,
    private val query: String
) : PagingSource<Int, MovieDbModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDbModel> {

        return withContext(Dispatchers.Default) {
            try {
                val currentPage = params.key ?: PAGE_NUMBER
                val response = apiService.searchMovie(query = query)
                if (response.isSuccessful) {
                    val result = response.body()?.movieDbModels as List<MovieDbModel>
                    LoadResult.Page(
                        data = result,
                        prevKey = if (currentPage > 1) currentPage - 1 else null,
                        nextKey = if (result.isEmpty()) null else currentPage + 1
                    )
                } else {
                    LoadResult.Error(HttpException(response))
                }
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieDbModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    companion object {

        const val PAGE_NUMBER = 1
    }
}