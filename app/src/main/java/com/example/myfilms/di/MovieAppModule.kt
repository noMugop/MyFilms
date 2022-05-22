package com.example.myfilms.di

import android.content.Context
import android.content.SharedPreferences
import com.example.myfilms.data.database.MovieDao
import com.example.myfilms.data.database.MovieDatabase
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.network.ApiService
import com.example.myfilms.data.repository.MovieRepositoryImpl
import com.example.myfilms.domain.MovieRepository
import com.example.myfilms.presentation.MainActivity
import com.example.myfilms.presentation.MainViewModel
import com.example.myfilms.presentation.fragments.details.DetailsViewModel
import com.example.myfilms.presentation.fragments.favorites.FavoritesViewModel
import com.example.myfilms.presentation.fragments.login.LoginViewModel
import com.example.myfilms.presentation.fragments.movies.MovieViewModel
import com.example.myfilms.presentation.fragments.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
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
    single<MovieRepository> { MovieRepositoryImpl(
        apiService = get(),
        db = get(),
        prefSettings = get(),
        editor = get()
    ) }
}

val viewModelModule = module {
    viewModel { MainViewModel(movieRepository = get()) }
    viewModel { DetailsViewModel(movieRepository = get()) }
    viewModel { LoginViewModel(movieRepository = get()) }
    viewModel { MovieViewModel(movieRepository = get()) }
    viewModel { FavoritesViewModel(movieRepository = get()) }
    viewModel { SettingsViewModel(movieRepository = get()) }
}

val movieAppModule = networkModule + daoModule + repositoryModule +
        viewModelModule + sharedPrefsModule

private fun getApiService(): ApiService = ApiFactory.getInstance()
private fun getMovieDao(context: Context): MovieDao = MovieDatabase.getInstance(context).movieDao()
private fun getPrefSettings(context: Context): SharedPreferences = context.getSharedPreferences(
    "Settings",
    Context.MODE_PRIVATE
)