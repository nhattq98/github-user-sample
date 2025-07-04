package com.tahn.assignment.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_user_remote_key")
internal data class GithubUserRemoteKeyEntity(
    @PrimaryKey val userId: Int,
    val prevKey: Int?,
    val nextKey: Int?,
)
