package com.tahn.assignment.remote

import com.tahn.assignment.model.remote.GithubUserResponse
import com.tahn.assignment.remote.api.GithubApiService

internal class GithubRemoteDataSourceImpl(
    private val api: GithubApiService,
) : GithubRemoteDataSource {
    override suspend fun fetchUsers(
        perPage: Int,
        since: Int,
    ): List<GithubUserResponse> = api.getUsers(perPage, since)

    override suspend fun fetchUserDetail(username: String) = api.getUserDetails(username)
}
