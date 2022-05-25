package com.example.myfilms.data.network.model.movie

import com.google.gson.annotations.SerializedName

data class AccountStatesDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("favorite")
    val favorite: Boolean,
    @SerializedName("rated")
    val rated: Any,
    @SerializedName("watchlist")
    val watchlist: Boolean
)
