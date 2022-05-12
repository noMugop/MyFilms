package com.example.myfilms.data.models.authorization

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginApprove(

    @SerializedName("username")
    var username: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("request_token")
    var request_token: String = ""
)