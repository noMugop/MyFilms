package com.example.myfilms.data.database.model.user

import androidx.room.ColumnInfo

data class AccountUpdateDbModel(

    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "avatar_uri")
    var avatar_uri: String
)
