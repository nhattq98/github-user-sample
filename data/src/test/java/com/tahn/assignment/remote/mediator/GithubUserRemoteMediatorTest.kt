package com.tahn.assignment.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.InitializeAction
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.withTransaction
import com.tahn.assignment.local.database.AppDatabase
import com.tahn.assignment.local.database.dao.GithubUserDao
import com.tahn.assignment.local.database.dao.GithubUserRemoteKeyDao
import com.tahn.assignment.local.database.entity.GithubUserEntity
import com.tahn.assignment.local.database.entity.GithubUserRemoteKeyEntity
import com.tahn.assignment.local.datastore.PreferencesDataStoreManager
import com.tahn.assignment.model.remote.GithubUserResponse
import com.tahn.assignment.model.toEntity
import com.tahn.assignment.remote.GithubRemoteDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
class GithubUserRemoteMediatorTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mediator: GithubUserRemoteMediator

    private val remoteDataSource: GithubRemoteDataSource = mockk()
    private val database: AppDatabase = mockk()
    private val userDao: GithubUserDao = mockk(relaxed = true)
    private val remoteKeyDao: GithubUserRemoteKeyDao = mockk(relaxed = true)
    private val dataStore: PreferencesDataStoreManager = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        mockkStatic(
            "androidx.room.RoomDatabaseKt",
        )

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }

        every { database.githubUserDao() } returns userDao
        every { database.githubUserRemoteKeyDao() } returns remoteKeyDao

        mediator = GithubUserRemoteMediator(remoteDataSource, database, dataStore)
    }

    @Test
    fun `initialize returns SKIP_INITIAL_REFRESH when cache is valid`() =
        runTest {
            val now = System.currentTimeMillis()
            val validTimestamp = now - TimeUnit.MINUTES.toMillis(10)

            coEvery { dataStore.lastUpdateTime } returns flowOf(validTimestamp)

            val result = mediator.initialize()

            assertEquals(InitializeAction.SKIP_INITIAL_REFRESH, result)
        }

    @Test
    fun `initialize returns LAUNCH_INITIAL_REFRESH when cache is expired`() =
        runTest {
            val now = System.currentTimeMillis()
            val expiredTimestamp = now - TimeUnit.MINUTES.toMillis(35)

            coEvery { dataStore.lastUpdateTime } returns flowOf(expiredTimestamp)

            val result = mediator.initialize()

            assertEquals(InitializeAction.LAUNCH_INITIAL_REFRESH, result)
        }

    @Test
    fun `refresh returns Success`() =
        runTest {
            // Given
            val userResponses = mockListResponse()
            coEvery { remoteDataSource.fetchUsers(any(), any()) } returns userResponses
            coEvery {
                dataStore.setLastUpdateTime(
                    any<Long>(),
                )
            } returns Unit
            val transactionLambda = slot<suspend () -> Unit>()
            coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
                transactionLambda.captured.invoke()
            }
            val pagingState = createPagingState(listOf())

            // When
            val result =
                mediator.load(
                    LoadType.REFRESH,
                    pagingState,
                )

            // Then
            coVerify { userDao.clearAll() }
            coVerify { userDao.insertUsers(any()) }
            coVerify { remoteKeyDao.clearAll() }
            coVerify { remoteKeyDao.insertAll(any()) }
            assertTrue(result is MediatorResult.Success)
            assertTrue(!(result as MediatorResult.Success).endOfPaginationReached)
        }

    @Test
    fun `refresh returns Error`() =
        runTest {
            // Given
            val pagingState = createPagingState(listOf())
            coEvery { remoteDataSource.fetchUsers(any(), any()) } throws Exception()

            // When
            val result = mediator.load(LoadType.REFRESH, pagingState)

            // Then
            assertTrue(result is MediatorResult.Error)
        }

    @Test
    fun `prepend return Success`() =
        runTest {
            // Given
            val pagingState = createPagingState(listOf())

            //  When
            val result =
                mediator.load(
                    LoadType.PREPEND,
                    pagingState,
                )

            // Then
            assertTrue(result is MediatorResult.Success)
            assertTrue((result as MediatorResult.Success).endOfPaginationReached)
        }

    // Append logic

    @Test
    fun `load append should Return Success When no lastItem`() =
        runTest {
            // Given
            val pagingState = createPagingState(listOf())

            // When
            val result = mediator.load(LoadType.APPEND, pagingState)

            // Then
            assertTrue(result is MediatorResult.Success)
            val success = result as MediatorResult.Success
            assertFalse(success.endOfPaginationReached)
        }

    @Test
    fun `load append returns Success with endOfPagination true when remoteKey is null`() {
        runTest {
            // Given
            val lastCacheGithubUserEntities =
                (0..40).toMockGithubUserResponseList().mapNotNull { it.toEntity() }

            val pagingState =
                PagingState<Int, GithubUserEntity>(
                    config = PagingConfig(pageSize = GithubUserRemoteMediator.PAGE_SIZE),
                    anchorPosition = 0,
                    pages =
                        listOf(
                            Page(
                                data = lastCacheGithubUserEntities,
                                prevKey = null,
                                nextKey = null,
                            ),
                        ),
                    leadingPlaceholderCount = 0,
                )

            coEvery {
                remoteKeyDao.getRemoteKeyByUserId(lastCacheGithubUserEntities.last().id)
            } returns null

            // When
            val result = mediator.load(LoadType.APPEND, pagingState)

            // Then
            assertTrue(result is MediatorResult.Success)
            assertTrue((result as MediatorResult.Success).endOfPaginationReached)

            // Verify no fetching/inserting
            coVerify(exactly = 1) {
                remoteKeyDao.getRemoteKeyByUserId(lastCacheGithubUserEntities.last().id)
            }
            coVerify(exactly = 0) { remoteDataSource.fetchUsers(any(), any()) }
            coVerify(exactly = 0) { userDao.insertUsers(any()) }
            coVerify(exactly = 0) { remoteKeyDao.insertAll(any()) }
        }
    }

    @Test
    fun `load append returns should Success with endOfPagination is false and lastItem existed`() {
        runTest {
            // Given
            val githubUserResponseList = (41..60).toMockGithubUserResponseList()
            val lastCacheGithubUserEntities =
                (0..40).toMockGithubUserResponseList().mapNotNull { it.toEntity() }
            val remoteKey =
                GithubUserRemoteKeyEntity(
                    userId = lastCacheGithubUserEntities.last().id,
                    prevKey = null,
                    nextKey = lastCacheGithubUserEntities.last().id,
                )
            val pagingState =
                PagingState<Int, GithubUserEntity>(
                    config = PagingConfig(pageSize = GithubUserRemoteMediator.PAGE_SIZE),
                    anchorPosition = 0,
                    pages =
                        listOf(
                            Page(
                                data = lastCacheGithubUserEntities,
                                prevKey = null,
                                nextKey = null,
                            ),
                        ),
                    leadingPlaceholderCount = 0,
                )

            coEvery {
                remoteDataSource.fetchUsers(
                    since = lastCacheGithubUserEntities.last().id,
                    perPage = GithubUserRemoteMediator.PAGE_SIZE,
                )
            } returns githubUserResponseList

            coEvery {
                remoteKeyDao.getRemoteKeyByUserId(any())
            } returns remoteKey

            // When
            val result =
                mediator.load(
                    LoadType.APPEND,
                    pagingState,
                )

            // Then
            assertTrue(result is MediatorResult.Success)
            assertFalse((result as MediatorResult.Success).endOfPaginationReached)
            coVerify { userDao.insertUsers(any()) }
            coVerify { remoteKeyDao.insertAll(any()) }
        }
    }

    @Test
    fun `load append returns Error when remote data source throws exception`() {
        runTest {
            // Given
            val lastCacheGithubUserEntities =
                (0..40).toMockGithubUserResponseList().mapNotNull { it.toEntity() }

            val remoteKey =
                GithubUserRemoteKeyEntity(
                    userId = lastCacheGithubUserEntities.last().id,
                    prevKey = null,
                    nextKey = lastCacheGithubUserEntities.last().id,
                )

            val pagingState =
                PagingState<Int, GithubUserEntity>(
                    config = PagingConfig(pageSize = GithubUserRemoteMediator.PAGE_SIZE),
                    anchorPosition = 0,
                    pages =
                        listOf(
                            Page(
                                data = lastCacheGithubUserEntities,
                                prevKey = null,
                                nextKey = null,
                            ),
                        ),
                    leadingPlaceholderCount = 0,
                )

            coEvery {
                remoteKeyDao.getRemoteKeyByUserId(lastCacheGithubUserEntities.last().id)
            } returns remoteKey

            coEvery {
                remoteDataSource.fetchUsers(
                    since = lastCacheGithubUserEntities.last().id,
                    perPage = GithubUserRemoteMediator.PAGE_SIZE,
                )
            } throws RuntimeException("Network error")

            // When
            val result = mediator.load(LoadType.APPEND, pagingState)

            // Then
            assertTrue(result is MediatorResult.Error)
            val error = result as MediatorResult.Error
            assertTrue(error.throwable is RuntimeException)
            assertEquals("Network error", error.throwable.message)

            // Verify flow
            coVerify { remoteKeyDao.getRemoteKeyByUserId(any()) }
            coVerify { remoteDataSource.fetchUsers(any(), any()) }
            coVerify(exactly = 0) { userDao.insertUsers(any()) }
            coVerify(exactly = 0) { remoteKeyDao.insertAll(any()) }
        }
    }

    @Test
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mockListResponse() =
        (0..20).map {
            GithubUserResponse(
                id = it,
                login = "login$it",
                avatarUrl = "avatar$it",
                url = "url$it",
            )
        }

    private fun IntRange.toMockGithubUserResponseList(): List<GithubUserResponse> =
        this.map {
            GithubUserResponse(
                id = it,
                login = "username$it",
                avatarUrl = "avatarUrl$it",
                url = "url$it",
            )
        }

    private fun createPagingState(pages: List<Page<Int, GithubUserEntity>>): PagingState<Int, GithubUserEntity> =
        PagingState(
            config = PagingConfig(pageSize = GithubUserRemoteMediator.PAGE_SIZE),
            anchorPosition = 0,
            pages = pages,
            leadingPlaceholderCount = 0,
        )
}
