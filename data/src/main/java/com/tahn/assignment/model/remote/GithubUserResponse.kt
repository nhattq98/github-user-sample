package com.tahn.assignment.model.remote

import com.google.gson.annotations.SerializedName

data class GithubUserResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("login")
    val login: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("url")
    val url: String?,
)
