package com.example.myfilms.presentation.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Result(

    @SerializedName("results")
    @Expose
    val movies: List<Movie>
)


