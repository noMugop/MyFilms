package com.example.myfilms.data.models

import com.google.gson.annotations.SerializedName

data class AccountStates(
    @SerializedName("id")
    val id: Int,
    @SerializedName("favorite")
    val favorite: Boolean,
    @SerializedName("rated")
    val rated: Any,
    @SerializedName("watchlist")
    val watchlist: Boolean
)
