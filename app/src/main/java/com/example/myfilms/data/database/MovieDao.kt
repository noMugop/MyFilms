package com.example.myfilms.data.database

import androidx.room.*
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.data.database.model.user.AccountUpdateDbModel
import com.example.myfilms.data.database.model.user.AccountDetailsDbModel


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
    suspend fun getAmountOfMovies(limit: Int, offset: Int, searchBy: String = ""): List<MovieDbModel>

    @Query("SELECT * FROM movies_table WHERE id == :movieId")
    suspend fun getMovieById(movieId: Int): MovieDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieList(movieDbModels: List<MovieDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movieDbModel: MovieDbModel)

//    @Update(entity = Movie::class)
//    suspend fun movieUpdate(movie: MovieUpdate)

    @Query("DELETE FROM movies_table")
    suspend fun deleteAllMovies()

    @Query("DELETE FROM movies_table WHERE id = :userId")
    suspend fun deleteMovieById(userId: Int)

    //users_table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AccountDetailsDbModel)

    @Query("SELECT * FROM users_table WHERE id == :userId")
    suspend fun getUserById(userId: Int): AccountDetailsDbModel

    @Update(entity = AccountDetailsDbModel::class)
    suspend fun userUpdate(user: AccountUpdateDbModel)
}