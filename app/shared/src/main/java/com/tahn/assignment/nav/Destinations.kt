package com.tahn.assignment.nav

import kotlinx.serialization.Serializable

@Serializable
object UserList

@Serializable
data class UserDetail(
    val username: String,
)
