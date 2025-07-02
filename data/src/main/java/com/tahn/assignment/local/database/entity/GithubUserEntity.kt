package com.tahn.assignment.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO: null check
// TODO: change name users -> user
@Entity(tableName = "github_users")
internal data class GithubUserEntity(
    @PrimaryKey val id: Long?,
    val login: String?,
    val avatarUrl: String?,
    val profileUrl: String?,
)
