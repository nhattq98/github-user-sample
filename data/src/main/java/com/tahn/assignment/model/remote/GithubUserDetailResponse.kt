package com.tahn.assignment.model.remote

import com.google.gson.annotations.SerializedName

internal data class GithubUserDetailResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("login")
    val login: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("html_url")
    val htmlUrl: String?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("followers")
    val followers: Int?,
    @SerializedName("following")
    val following: Int?,
)
