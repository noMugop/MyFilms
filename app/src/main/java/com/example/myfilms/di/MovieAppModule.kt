package com.example.myfilms.di

import android.content.Context
import android.content.SharedPreferences
import com.example.myfilms.data.database.MovieDao
import com.example.myfilms.data.database.MovieDatabase
import com.example.myfilms.data.network.ApiFactory
import com.example.myfilms.data.network.ApiService
import com.example.myfilms.data.repository.RepositoryImpl
import com.example.myfilms.presentation.MainViewModel
import com.example.myfilms.presentation.fragments.details.DetailsViewModel
import com.example.myfilms.presentation.fragments.favorites.FavoritesViewModel
import com.example.myfilms.presentation.fragments.login.LoginViewModel
import com.example.myfilms.presentation.fragments.movies.MovieViewModel
import com.example.myfilms.presentation.fragments.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

    val networkModule = module {
        single { getApiService() }
    }

    val daoModule = module {
        single { getMovieDao(context = get()) }
    }

    val repositoryModule = module {
        single { getPrefSettings(context = get()) }
        single { RepositoryImpl(apiService = get(), db = get(), prefSettings = get()) }
    }

    val viewModelModule = module {
        viewModel { MainViewModel(repository = get())}
        viewModel { DetailsViewModel(repository = get())}
        viewModel { LoginViewModel(repository = get(), application = get())}
        viewModel { MovieViewModel(repository = get())}
        viewModel { FavoritesViewModel(repository = get(), application = get())}
        viewModel { SettingsViewModel(repository = get())}
    }

    val appModule = networkModule + daoModule + repositoryModule + viewModelModule

    private fun getApiService(): ApiService = ApiFactory.getInstance()
    private fun getMovieDao(context: Context): MovieDao = MovieDatabase.getInstance(context).movieDao()
    private fun getPrefSettings(context: Context): SharedPreferences = context.getSharedPreferences(
        "Settings",
        Context.MODE_PRIVATE
    )


