package com.example.myfilms.data.database

import androidx.room.*
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.data.models.movie.MovieUpdate
import com.example.myfilms.data.models.account.AccountUpdate
import com.example.myfilms.data.models.account.DbAccountDetails


@Dao
interface MovieDao {

//    @Query("SELECT * FROM movies_table ORDER BY voteAverage DESC")
//    suspend fun getMovieList(): List<Movie>

    //movies_table
    @Query(
        "SELECT * FROM movies_table " +
                "WHERE :searchBy = '' OR title LIKE '%' || :searchBy || '%' " +
                "ORDER BY voteAverage DESC " +
                "LIMIT :limit OFFSET :offset"
    )
    suspend fun getAmountOfMovies(limit: Int, offset: Int, searchBy: String = ""): List<Movie>

    @Query("SELECT * FROM movies_table WHERE id == :movieId")
    suspend fun getMovieById(movieId: Int): Movie

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieList(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

//    @Update(entity = Movie::class)
//    suspend fun movieUpdate(movie: MovieUpdate)

    @Query("DELETE FROM movies_table")
    suspend fun deleteAllMovies()

    @Query("DELETE FROM movies_table WHERE id = :userId")
    suspend fun deleteMovieById(userId: Int)

    //users_table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: DbAccountDetails)

    @Query("SELECT * FROM users_table WHERE id == :userId")
    suspend fun getUserById(userId: Int): DbAccountDetails

    @Update(entity = DbAccountDetails::class)
    suspend fun userUpdate(user: AccountUpdate)
}