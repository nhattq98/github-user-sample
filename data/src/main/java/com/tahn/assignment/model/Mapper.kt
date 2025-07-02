package com.tahn.assignment.model

import com.tahn.assignment.local.database.entity.GithubUserEntity
import com.tahn.assignment.model.remote.GithubUserResponse

internal fun GithubUserResponse.toEntity(): GithubUserEntity =
    GithubUserEntity(
        id = id ?: -1,
        login = login,
        avatarUrl = avatarUrl,
        profileUrl = url,
    )

internal fun GithubUserEntity.toDomain(): GithubUser =
    GithubUser(
        id = id,
        username = login,
        avatarUrl = avatarUrl,
        profileUrl = profileUrl,
    )
