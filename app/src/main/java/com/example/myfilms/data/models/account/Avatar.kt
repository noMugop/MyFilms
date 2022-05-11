package com.example.myfilms.data.models.account

import com.google.gson.annotations.SerializedName


data class Avatar(
    @SerializedName("tmdb")
    val tmdb: Tmdb? = null
)
