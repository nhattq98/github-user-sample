package com.tahn.assignment.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_user_remote_key")
internal data class GithubUserRemoteKeyEntity(
    @PrimaryKey val userId: Long,
    val prevKey: Long?,
    val nextKey: Long?,
)
