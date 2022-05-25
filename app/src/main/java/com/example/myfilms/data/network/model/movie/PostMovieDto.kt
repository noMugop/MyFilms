package com.example.myfilms.data.network.model.movie

import com.google.gson.annotations.SerializedName

data class PostMovieDto(

    @SerializedName("media_type")
    val media_type: String = "movie",
    @SerializedName("media_id")
    var media_id: Int,
    @SerializedName("favorite")
    var isFavorite: Boolean
)
