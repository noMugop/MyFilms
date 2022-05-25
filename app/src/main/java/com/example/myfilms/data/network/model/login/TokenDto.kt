package com.example.myfilms.data.network.model.login

import com.google.gson.annotations.SerializedName

data class TokenDto(

//    @SerializedName("success")
//    val success: Boolean,
//    @SerializedName("expires_at")
//    val expires_at: String,
    @SerializedName("request_token")
    val request_token: String
)