package com.tahn.assignment.model

import com.tahn.assignment.local.database.entity.GithubUserEntity
import com.tahn.assignment.model.remote.GithubUserDetailResponse
import com.tahn.assignment.model.remote.GithubUserResponse

internal fun GithubUserResponse.toEntity(): GithubUserEntity? =
    if (id == null) {
        null
    } else {
        GithubUserEntity(
            id = id,
            login = login,
            avatarUrl = avatarUrl,
            profileUrl = url,
        )
    }

internal fun GithubUserEntity.toDomain(): GithubUser =
    GithubUser(
        id = id,
        username = login,
        avatarUrl = avatarUrl,
        profileUrl = profileUrl,
    )

internal fun GithubUserDetailResponse.toDomain(): GithubUserDetail =
    GithubUserDetail(
        id = id ?: -1,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        location = location,
        followers = followers,
        following = following,
    )
