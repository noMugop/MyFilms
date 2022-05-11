package com.example.myfilms.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostMovie(

    @SerializedName("media_type")
    val media_type: String = "movie",
    @SerializedName("media_id")
    var media_id: Int,
    @SerializedName("favorite")
    var isFavorite: Boolean
)
