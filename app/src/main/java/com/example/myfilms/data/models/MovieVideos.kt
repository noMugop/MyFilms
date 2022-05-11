package com.example.myfilms.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieVideos(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("results")
    val list: List<ResultVideos>? = null
)
