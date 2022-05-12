package com.example.myfilms.data.models.authorization

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class Session(

//    @SerializedName("success")
//    val success: Boolean,
    @SerializedName("session_id")
    var session_id: String = ""
)