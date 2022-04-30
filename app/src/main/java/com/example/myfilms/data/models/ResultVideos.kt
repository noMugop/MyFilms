package com.example.myfilms.data.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResultVideos(

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("key")
    @Expose
    val key: String
)
