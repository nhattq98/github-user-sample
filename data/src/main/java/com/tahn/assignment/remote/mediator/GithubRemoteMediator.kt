package com.tahn.assignment.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.tahn.assignment.local.database.AppDatabase
import com.tahn.assignment.local.database.entity.GithubUserEntity
import com.tahn.assignment.local.datastore.PreferencesDataStoreManager
import com.tahn.assignment.remote.GithubRemoteDataSource
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
internal class UserRemoteMediator(
    private val remoteDataSource: GithubRemoteDataSource,
    private val database: AppDatabase,
    private val dataStore: PreferencesDataStoreManager,
) : RemoteMediator<Int, GithubUserEntity>() {
    private val githubUserDao = database.githubUserDao()
    private val remoteKeyDao = database.githubUserRemoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(CACHE_TIMEOUT, TimeUnit.MINUTES)
        val lastUpdate =
            dataStore.lastUpdateTime.first() ?: return InitializeAction.LAUNCH_INITIAL_REFRESH
        return if (System.currentTimeMillis() - lastUpdate <= cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    companion object {
        const val CACHE_TIMEOUT = 30L
    }
}
