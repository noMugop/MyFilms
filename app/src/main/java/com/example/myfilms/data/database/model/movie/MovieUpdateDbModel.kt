package com.example.myfilms.data.database.model.movie

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class MovieUpdateDbModel(

    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean
)