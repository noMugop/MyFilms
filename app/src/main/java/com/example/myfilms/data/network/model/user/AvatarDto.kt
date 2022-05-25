package com.example.myfilms.data.network.model.user

import com.google.gson.annotations.SerializedName


data class AvatarDto(
    @SerializedName("tmdb")
    val tmdbDto: TmdbDto? = null
)
