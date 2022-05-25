package com.example.myfilms.domain.usecase

import com.example.myfilms.domain.repository.MovieRepository

class GetFavoritesFromNetworkUseCase(private val movieRepository: MovieRepository) {

    suspend operator fun invoke() = movieRepository.getFavoritesFromNetwork()
}