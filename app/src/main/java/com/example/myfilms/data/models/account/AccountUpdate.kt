package com.example.myfilms.data.models.account

import androidx.room.ColumnInfo

data class AccountUpdate(

    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "avatar_uri")
    var avatar_uri: String
)
