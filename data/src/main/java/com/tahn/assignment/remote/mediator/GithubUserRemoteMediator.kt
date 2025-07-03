package com.tahn.assignment.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.tahn.assignment.local.database.AppDatabase
import com.tahn.assignment.local.database.entity.GithubUserEntity
import com.tahn.assignment.local.database.entity.GithubUserRemoteKeyEntity
import com.tahn.assignment.local.datastore.PreferencesDataStoreManager
import com.tahn.assignment.model.toEntity
import com.tahn.assignment.remote.GithubRemoteDataSource
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
internal class GithubUserRemoteMediator(
    private val remoteDataSource: GithubRemoteDataSource,
    private val database: AppDatabase,
    private val dataStore: PreferencesDataStoreManager,
) : RemoteMediator<Int, GithubUserEntity>() {
    private val githubUserDao = database.githubUserDao()
    private val remoteKeyDao = database.githubUserRemoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        val cacheTimeoutMillis = TimeUnit.MINUTES.toMillis(CACHE_TIMEOUT)
        val lastFetchTime = dataStore.lastUpdateTime.first()
        val isCacheValid = System.currentTimeMillis() - lastFetchTime <= cacheTimeoutMillis

        return if (isCacheValid) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GithubUserEntity>,
    ): MediatorResult {
        val since =
            when (loadType) {
                LoadType.REFRESH -> START_SINCE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(false)
                    val remoteKey = remoteKeyDao.getRemoteKeyByUserId(lastItem.id)
                    remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

        return try {
            val response = remoteDataSource.fetchUsers(perPage = state.config.pageSize, since = since)
            val users = response.mapNotNull { it.toEntity() }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    dataStore.setLastUpdateTime(System.currentTimeMillis())
                    githubUserDao.clearAll()
                    remoteKeyDao.clearAll()
                }
                if (users.isNotEmpty()) {
                    githubUserDao.insertUsers(users)

                    val keys =
                        users.map {
                            GithubUserRemoteKeyEntity(
                                userId = it.id,
                                prevKey = null,
                                nextKey = users.last().id,
                            )
                        }
                    remoteKeyDao.insertAll(keys)
                }
            }

            MediatorResult.Success(endOfPaginationReached = users.size < state.config.pageSize)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        const val CACHE_TIMEOUT = 30L
        private const val START_SINCE = 0
        const val PAGE_SIZE = 20
    }
}
