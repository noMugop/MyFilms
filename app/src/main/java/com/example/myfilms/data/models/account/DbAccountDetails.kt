package com.example.myfilms.data.models.account

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users_table")
data class DbAccountDetails(

    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,
    val avatar: String? = null,
    val iso639: String? = null,
    val iso3166: String? = null,
    val name: String? = null,
    val includeAdult: Boolean? = null,
    val username: String = "Гость",
    var avatar_uri: String? = null
)
