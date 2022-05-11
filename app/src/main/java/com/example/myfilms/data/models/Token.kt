package com.example.myfilms.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Token(

//    @SerializedName("success")
//    val success: Boolean,
//    @SerializedName("expires_at")
//    val expires_at: String,
    @SerializedName("request_token")
    val request_token: String
)