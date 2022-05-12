package com.example.myfilms.data.models.movie
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "movies_table")
@Parcelize
data class Movie(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("vote_count")
    val voteCount: Int? = null,

    @SerializedName("vote_average")
    val voteAverage: Float? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("original_title")
    val originalTitle: String? = null,

    @SerializedName("overview")
    val overview: String? = null,

    @SerializedName("popularity")
    val popularity: Float? = null,

    @SerializedName("poster_path")
    val posterPath: String? = null,

    @SerializedName("backdrop_path")
    val backdropPath: String? = null,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("homepage")
    val homepage: String? = null,

    @SerializedName("tagline")
    val tagline: String? = null,

    var isFavorite: Boolean = false

): Parcelable
