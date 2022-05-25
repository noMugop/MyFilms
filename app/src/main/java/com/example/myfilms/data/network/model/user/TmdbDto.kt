package com.example.myfilms.data.network.model.user

import com.google.gson.annotations.SerializedName

data class TmdbDto(
    @SerializedName("avatar_path")
    val avatarPath: String? = null
)
