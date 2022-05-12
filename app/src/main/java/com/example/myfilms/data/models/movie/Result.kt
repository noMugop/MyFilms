package com.example.myfilms.data.models.movie

import com.google.gson.annotations.SerializedName

data class Result(

    @SerializedName("results")
    val movies: List<Movie>
)


