package com.example.myfilms.domain.usecase

import com.example.myfilms.domain.repository.MovieRepository

class DeleteLoginSessionUseCase(private val movieRepository: MovieRepository) {

    operator fun invoke() {
        movieRepository.deleteLoginSession()
    }
}