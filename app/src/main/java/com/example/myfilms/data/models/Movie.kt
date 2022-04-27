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
    var id: Int? = null,

    @SerializedName("vote_count")
    @Expose
    val voteCount: Int? = null,

    @SerializedName("title")
    @Expose
    val title: String? = null,

    @SerializedName("original_title")
    @Expose
    val originalTitle: String? = null,

    @SerializedName("overview")
    @Expose
    val overview: String? = null,

    @SerializedName("popularity")
    @Expose
    val popularity: Float? = null,

    @SerializedName("poster_path")
    @Expose
    val posterPath: String? = null,

    @SerializedName("backdrop_path")
    @Expose
    val backdropPath: String? = null,

    @SerializedName("release_date")
    @Expose
    val releaseDate: String? = null,

    @SerializedName("homepage")
    @Expose
    val homepage: String? = null,

    @SerializedName("tagline")
    @Expose
    val tagline: String? = null,

    var isFavorite: Boolean = false

): Parcelable
