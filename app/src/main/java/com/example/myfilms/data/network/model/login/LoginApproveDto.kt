package com.example.myfilms.data.network.model.login

import com.google.gson.annotations.SerializedName

data class LoginApproveDto(

    @SerializedName("username")
    var username: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("request_token")
    var request_token: String = ""
)