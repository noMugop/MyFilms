package com.example.myfilms.di

import android.content.Context
import android.content.SharedPreferences
import com.example.myfilms.data.database.MovieDao
import com.example.myfilms.data.database.MovieDatabase
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.network.ApiService
import com.example.myfilms.data.repository_impl.MovieRepositoryImpl
import com.example.myfilms.domain.repository.MovieRepository
import com.example.myfilms.domain.usecase.*
import com.example.myfilms.presentation.MainViewModel
import com.example.myfilms.presentation.fragments.details.DetailsViewModel
import com.example.myfilms.presentation.fragments.favorites.FavoritesViewModel
import com.example.myfilms.presentation.fragments.login.LoginViewModel
import com.example.myfilms.presentation.fragments.movies.MovieViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { getApiService() }
}

val daoModule = module {
    single { getMovieDao(context = get()) }
}

val sharedPrefsModule = module {
    single { getPrefSettings(context = get()) }
    single { getPrefSettings(context = get()).edit() }
}

val repositoryModule = module {
    single<MovieRepository> {
        MovieRepositoryImpl(
            apiService = get(),
            db = get(),
            prefSettings = get(),
            editor = get()
        )
    }
}

val useCaseModule = module {
    single { AddOrDeleteFavoriteUseCase(movieRepository = get()) }
    single { AddUserUseCase(movieRepository = get()) }
    single { DeleteFavoriteMoviesUseCase(movieRepository = get()) }
    single { DeleteMainSessionUseCase(movieRepository = get()) }
    single { GetFavoriteMovieByIdUseCase(movieRepository = get()) }
    single { GetFavoritesFromDbUseCase(movieRepository = get()) }
    single { GetFavoritesFromNetworkUseCase(movieRepository = get()) }
    single { GetMainSessionUseCase(movieRepository = get()) }
    single { GetMoviesFromNetworkUseCase(movieRepository = get()) }
    single { GetTrailerUseCase(movieRepository = get()) }
    single { GetUserUseCase(movieRepository = get()) }
    single { LoginUseCase(movieRepository = get()) }
    single { UpdateUserUseCase(movieRepository = get()) }
}

val viewModelModule = module {
    viewModel {
        MainViewModel(
            getUserUseCase = get(),
            getMainSessionUseCase = get(),
            deleteMainSessionUseCase = get()
        )
    }
    viewModel {
        LoginViewModel(
            loginUseCase = get(),
            addUserUseCase = get(),
            getFavoritesFromNetworkUseCase = get(),
            getMainSessionUseCase = get(),
            deleteMainSessionUseCase = get(),
            deleteFavoriteMoviesUseCase = get(),
            getUserUseCase = get(),
            updateUserUseCase = get()
        )
    }
    viewModel {
        MovieViewModel(
            getMoviesFromNetworkUseCase = get(),
            deleteMainSessionUseCase = get()
        )
    }
    viewModel {
        FavoritesViewModel(
            getFavoritesFromDbUseCase = get(),
            getMainSessionUseCase = get(),
            deleteMainSessionUseCase = get()
        )
    }
    viewModel {
        DetailsViewModel(
            getFavoriteMovieByIdUseCase = get(),
            getTrailerUseCase = get(),
            addOrDeleteFavoriteUseCase = get()
        )
    }
}

val movieAppModule = networkModule + daoModule + sharedPrefsModule +
        repositoryModule + useCaseModule + viewModelModule

private fun getApiService(): ApiService = ApiFactory.getInstance()
private fun getMovieDao(context: Context): MovieDao = MovieDatabase.getInstance(context).movieDao()
private fun getPrefSettings(context: Context): SharedPreferences = context.getSharedPreferences(
    "Settings",
    Context.MODE_PRIVATE
)