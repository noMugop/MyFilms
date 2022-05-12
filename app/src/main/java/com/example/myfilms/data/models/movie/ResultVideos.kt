package com.example.myfilms.data.models.movie

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResultVideos(

    @SerializedName("name")
    val name: String,

    @SerializedName("key")
    val key: String
)
