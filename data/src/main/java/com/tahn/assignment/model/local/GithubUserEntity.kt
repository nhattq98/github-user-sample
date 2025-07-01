package com.tahn.assignment.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO: null check
@Entity(tableName = "github_users")
data class GithubUserEntity(
    @PrimaryKey val id: Long?,
    val login: String?,
    val avatarUrl: String?,
    val profileUrl: String?,
)
