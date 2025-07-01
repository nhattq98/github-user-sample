package com.tahn.assignment.remote

import com.tahn.assignment.model.remote.GithubUserResponse

interface GithubRemoteDataSource {
    suspend fun fetchUsers(perPage: Int, since: Long): List<GithubUserResponse>
}