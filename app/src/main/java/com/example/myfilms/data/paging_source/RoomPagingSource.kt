package com.example.myfilms.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myfilms.data.database.MovieDao
import com.example.myfilms.data.database.model.movie.MovieDbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class RoomPagingSource(
    private val db: MovieDao,
    private val pageSize: Int,
    private val searchBy: String
) : PagingSource<Int, MovieDbModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieDbModel> {

        return withContext(Dispatchers.Default) {
            try {
                val currentPage = params.key ?: PAGE_NUMBER
                val offset = currentPage * pageSize
                val movies = db.getAmountOfMovies(pageSize, offset, searchBy)
                LoadResult.Page(
                    data = movies,
                    prevKey = if (currentPage == 0) null else currentPage - 1,
                    nextKey = if (movies.isEmpty()) null else currentPage + 1
                )
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

        const val PAGE_NUMBER = 0
    }
}