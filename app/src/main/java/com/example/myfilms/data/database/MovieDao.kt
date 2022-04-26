package com.example.myfilms.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myfilms.data.models.Movie

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie_table")
    fun getMovieList(): LiveData<List<Movie>>

    @Query("SELECT * FROM movie_table WHERE id == :movieId")
    fun getMovieById(movieId: Int): LiveData<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun loadData(movies: List<Movie>)
}