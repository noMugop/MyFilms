package com.example.myfilms.data.network.model.login

import com.google.gson.annotations.SerializedName

data class SessionDto(

//    @SerializedName("success")
//    val success: Boolean,
    @SerializedName("session_id")
    var session_id: String = ""
)