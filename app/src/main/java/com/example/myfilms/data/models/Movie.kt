package com.example.myfilms.data.models
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "movie_table")
@Parcelize
data class Movie(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @Expose
    var id: Int,

    @SerializedName("vote_count")
    @Expose
    val voteCount: Int,

    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("original_title")
    @Expose
    val originalTitle: String,

    @SerializedName("overview")
    @Expose
    val overview: String,

    @SerializedName("poster_path")
    @Expose
    val posterPath: String,

    @SerializedName("backdrop_path")
    @Expose
    val backdropPath: String,

    @SerializedName("release_date")
    @Expose
    val releaseDate: String,

    @SerializedName("homepage")
    @Expose
    val homepage: String? = null,

    @SerializedName("tagline")
    @Expose
    val tagline: String? = null,

    var isFavorite: Boolean = false

): Parcelable
