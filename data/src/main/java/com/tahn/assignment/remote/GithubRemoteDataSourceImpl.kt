package com.tahn.assignment.remote

import com.tahn.assignment.model.remote.GithubUserResponse
import com.tahn.assignment.remote.api.GithubApiService

internal class GithubRemoteDataSourceImpl(
    private val api: GithubApiService,
) : GithubRemoteDataSource {
    override suspend fun fetchUsers(
        perPage: Long,
        since: Int,
    ): List<GithubUserResponse> = api.getUsers(perPage, since)
}
