package com.example.myfilms.domain.usecase

import com.example.myfilms.domain.repository.MovieRepository

class GetTrailerUseCase(private val movieRepository: MovieRepository) {

    suspend operator fun invoke(movieId: Int) = movieRepository.getTrailer(movieId)
}