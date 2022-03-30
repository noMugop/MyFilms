package com.example.myfilms.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginApprove(

    @SerializedName("username")
    @Expose
    var username: String = "noMugop",
    @SerializedName("password")
    @Expose
    var password: String = "10poguMon",
    @SerializedName("request_token")
    @Expose
    var request_token: String
)