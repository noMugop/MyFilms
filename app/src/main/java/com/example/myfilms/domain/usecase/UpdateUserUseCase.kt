package com.example.myfilms.domain.usecase

import com.example.myfilms.data.database.model.user.AccountDetailsDbModel
import com.example.myfilms.domain.repository.MovieRepository

class UpdateUserUseCase(private val movieRepository: MovieRepository) {

    suspend operator fun invoke(user: AccountDetailsDbModel) = movieRepository.updateUser(user)
}