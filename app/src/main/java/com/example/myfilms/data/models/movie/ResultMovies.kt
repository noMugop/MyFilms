package com.example.myfilms.data.models.movie

import com.google.gson.annotations.SerializedName

data class ResultMovies(

    @SerializedName("results")
    val movies: List<Movie>
)


