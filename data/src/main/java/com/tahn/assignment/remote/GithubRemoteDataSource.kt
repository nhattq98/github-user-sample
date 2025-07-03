package com.tahn.assignment.remote

import com.tahn.assignment.model.remote.GithubUserDetailResponse
import com.tahn.assignment.model.remote.GithubUserResponse

internal interface GithubRemoteDataSource {
    suspend fun fetchUsers(
        perPage: Int,
        since: Int,
    ): List<GithubUserResponse>

    suspend fun fetchUserDetail(username: String): GithubUserDetailResponse
}
