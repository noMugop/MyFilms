package com.example.myfilms.domain.usecase

import com.example.myfilms.domain.repository.MovieRepository

class GetFavoriteMovieByIdUseCase(private val movieRepository: MovieRepository) {

    suspend operator fun invoke(movieId: Int) = movieRepository.getFavoriteMovieById(movieId)
}