package com.example.myfilms.data.database

import androidx.room.*
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.MovieUpdate


@Dao
interface MovieDao {

    @Query("SELECT * FROM movie_table GROUP BY voteCount")
    suspend fun getMovieList(): List<Movie>

    @Query("SELECT * FROM movie_table WHERE id == :movieId")
    suspend fun getMovieById(movieId: Int): Movie

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieList(movies: List<Movie>)

    @Update(entity = Movie::class)
    suspend fun update(movie: MovieUpdate)
}