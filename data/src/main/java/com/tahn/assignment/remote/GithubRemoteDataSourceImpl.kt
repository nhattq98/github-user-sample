package com.tahn.assignment.remote

import com.tahn.assignment.model.remote.GithubUserResponse
import com.tahn.assignment.remote.api.GithubApi

class GithubRemoteDataSourceImpl(
    private val api: GithubApi,
) : GithubRemoteDataSource {
    override suspend fun fetchUsers(
        perPage: Int,
        since: Long,
    ): List<GithubUserResponse> = api.getUsers(perPage, since)
}
