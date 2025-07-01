package com.tahn.assignment.model

import com.tahn.assignment.model.local.GithubUserEntity
import com.tahn.assignment.model.remote.GithubUserResponse

fun GithubUserResponse.toEntity(): GithubUserEntity =
    GithubUserEntity(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        profileUrl = url,
    )

fun GithubUserEntity.toDomain(): GithubUser =
    GithubUser(
        id = id,
        username = login,
        avatarUrl = avatarUrl,
        profileUrl = profileUrl,
    )
