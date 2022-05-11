package com.example.myfilms.data.database

import androidx.room.*
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.MovieUpdate
import com.example.myfilms.data.models.account.AccountUpdate
import com.example.myfilms.data.models.account.DbAccountDetails


@Dao
interface MovieDao {

    //movies_table
    @Query("SELECT * FROM movies_table ORDER BY voteAverage DESC")
    suspend fun getMovieList(): List<Movie>

    @Query("SELECT * FROM movies_table WHERE id == :movieId")
    suspend fun getMovieById(movieId: Int): Movie

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieList(movies: List<Movie>)

    @Update(entity = Movie::class)
    suspend fun movieUpdate(movie: MovieUpdate)

    //users_table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: DbAccountDetails)

    @Query("SELECT * FROM users_table WHERE id == :userId")
    suspend fun getUserById(userId: Int): DbAccountDetails

    @Update(entity = DbAccountDetails::class)
    suspend fun userUpdate(user: AccountUpdate)
}