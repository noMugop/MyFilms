package com.example.myfilms.data.network.model.login

import com.google.gson.annotations.SerializedName

data class TokenDto(

    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("expires_at")
    val expires_at: String? = null,
    @SerializedName("request_token")
    val request_token: String
)