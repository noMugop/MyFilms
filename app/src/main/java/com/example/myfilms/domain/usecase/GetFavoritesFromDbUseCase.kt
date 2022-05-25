package com.example.myfilms.domain.usecase

import com.example.myfilms.domain.repository.MovieRepository

class GetFavoritesFromDbUseCase(private val movieRepository: MovieRepository) {

    operator fun invoke(searchBy: String) = movieRepository.getFavoritesFromDB(searchBy)
}