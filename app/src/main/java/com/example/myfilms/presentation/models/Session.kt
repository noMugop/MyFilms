package com.example.myfilms.presentation.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Session(

//    @SerializedName("success")
//    @Expose
//    val success: Boolean,
    @SerializedName("session_id")
    @Expose
    val session_id: String
)
