package com.example.myfilms.data.network.model.movie

import com.google.gson.annotations.SerializedName

data class MovieTrailerDto(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("results")
    val list: List<ResultTrailerDto>? = null
)
