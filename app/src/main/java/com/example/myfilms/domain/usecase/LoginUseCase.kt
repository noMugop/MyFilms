package com.example.myfilms.domain.usecase

import com.example.myfilms.domain.repository.MovieRepository

class LoginUseCase(private val movieRepository: MovieRepository) {

    suspend operator fun invoke(username: String, password: String) =
        movieRepository.login(username, password)
}