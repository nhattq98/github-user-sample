package com.tahn.assignment.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_users")
internal data class GithubUserEntity(
    @PrimaryKey val id: Int,
    val login: String?,
    val avatarUrl: String?,
    val profileUrl: String?,
)
