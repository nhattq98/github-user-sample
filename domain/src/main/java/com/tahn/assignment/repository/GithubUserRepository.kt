package com.tahn.assignment.repository

import androidx.paging.PagingData
import com.tahn.assignment.model.GithubUser
import kotlinx.coroutines.flow.Flow

interface GithubUserRepository {
    fun getGithubUsersPaging(): Flow<PagingData<GithubUser>>
}
