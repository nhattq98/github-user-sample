package com.tahn.assignment.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.tahn.assignment.local.database.AppDatabase
import com.tahn.assignment.local.datastore.PreferencesDataStoreManager
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.model.toDomain
import com.tahn.assignment.remote.GithubRemoteDataSource
import com.tahn.assignment.remote.mediator.GithubUserRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class GithubUserRepositoryImpl(
    private val remoteDataSource: GithubRemoteDataSource,
    private val database: AppDatabase,
    private val dataStore: PreferencesDataStoreManager,
) : GithubUserRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getGithubUsersPaging(): Flow<PagingData<GithubUser>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = GithubUserRemoteMediator.PAGE_SIZE,
                    initialLoadSize = GithubUserRemoteMediator.PAGE_SIZE,
                    prefetchDistance = 10,
                ),
            remoteMediator =
                GithubUserRemoteMediator(
                    remoteDataSource,
                    database,
                    dataStore,
                ),
            pagingSourceFactory = { database.githubUserDao().getUserPagingSource() },
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun getGithubUserDetail(username: String) =
        flow {
            val response = remoteDataSource.fetchUserDetail(username)
            emit(response.toDomain())
        }
}
