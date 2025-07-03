package com.tahn.assignment.repository

import androidx.paging.PagingData
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.model.GithubUserDetail
import kotlinx.coroutines.flow.Flow

interface GithubUserRepository {
    fun getGithubUsersPaging(): Flow<PagingData<GithubUser>>

    fun getGithubUserDetail(username: String): Flow<GithubUserDetail>
}
