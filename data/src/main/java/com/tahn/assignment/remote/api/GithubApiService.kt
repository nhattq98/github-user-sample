package com.tahn.assignment.remote.api

import com.tahn.assignment.model.remote.GithubUserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApiService {
    @GET("users")
    suspend fun getUsers(
        @Query("per_page") perPage: Int,
        @Query("since") since: Long,
    ): List<GithubUserResponse>
}
