package com.example.myfilms.data.network.model.movie

import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.google.gson.annotations.SerializedName

data class ResultMoviesDto(

    @SerializedName("results")
    val movieDbModels: List<MovieDbModel>
)


