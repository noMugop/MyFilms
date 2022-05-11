package com.example.myfilms.data.models.account

import com.google.gson.annotations.SerializedName

data class AccountDetails(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("avatar")
    val avatar: Avatar? = null,

    @SerializedName("iso_639_1")
    val iso639: String? = null,

    @SerializedName("iso_3166_1")
    val iso3166: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("include_adult")
    val includeAdult: Boolean? = null,

    @SerializedName("username")
    val username: String = "Гость"
)
