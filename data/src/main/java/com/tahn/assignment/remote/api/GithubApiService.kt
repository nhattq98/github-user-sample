package com.tahn.assignment.remote.api

import com.tahn.assignment.model.remote.GithubUserDetailResponse
import com.tahn.assignment.model.remote.GithubUserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Documentation: https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28
internal interface GithubApiService {
    @GET("users")
    suspend fun getUsers(
        @Query("per_page") perPage: Int,
        @Query("since") since: Int,
    ): List<GithubUserResponse>

    @GET("users/{username}")
    suspend fun getUserDetails(
        @Path("username") username: String,
    ): GithubUserDetailResponse
}
