package com.example.myfilms.domain.usecase

import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.domain.repository.MovieRepository

class AddOrDeleteFavoriteUseCase(private val movieRepository: MovieRepository) {

    suspend operator fun invoke(movieDbModel: MovieDbModel) =
        movieRepository.addOrDeleteFavorite(movieDbModel)
}