package com.example.myfilms.data.models.movie

import com.google.gson.annotations.SerializedName

data class MovieVideos(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("results")
    val list: List<ResultTrailers>? = null
)
