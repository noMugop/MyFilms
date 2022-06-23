package com.example.myfilms.domain.usecase

import com.example.myfilms.domain.repository.MovieRepository

class SearchMoviesUseCase(private val movieRepository: MovieRepository) {

    operator fun invoke(query: String) = movieRepository.searchMovies(query)
}