package com.example.myfilms.data.network.model.movie

import com.google.gson.annotations.SerializedName

data class ResultTrailerDto(

    @SerializedName("name")
    val name: String,

    @SerializedName("key")
    val key: String
)
