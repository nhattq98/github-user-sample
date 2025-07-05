package com.tahn.assignment.repository

import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import androidx.room.withTransaction
import com.tahn.assignment.dispatcher.DispatcherProvider
import com.tahn.assignment.local.database.AppDatabase
import com.tahn.assignment.local.datastore.PreferencesDataStoreManager
import com.tahn.assignment.model.remote.GithubUserDetailResponse
import com.tahn.assignment.model.remote.GithubUserResponse
import com.tahn.assignment.model.toDomain
import com.tahn.assignment.model.toEntity
import com.tahn.assignment.remote.GithubRemoteDataSource
import com.tahn.assignment.remote.api.GithubApiService
import com.tahn.assignment.remote.mediator.GithubUserRemoteMediator
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

class TestDispatcherProvider(
    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher(),
) : DispatcherProvider {
    override fun io(): CoroutineDispatcher = testDispatcher

    override fun default(): CoroutineDispatcher = testDispatcher

    override fun main(): CoroutineDispatcher = testDispatcher

    override fun immediate(): CoroutineDispatcher = testDispatcher
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class GithubUserRepositoryImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider = TestDispatcherProvider(testDispatcher)

    private lateinit var api: GithubApiService
    private lateinit var remoteDataSource: GithubRemoteDataSource
    private lateinit var database: AppDatabase
    private lateinit var datastore: PreferencesDataStoreManager
    private lateinit var mediator: GithubUserRemoteMediator
    private lateinit var repository: GithubUserRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkStatic("androidx.room.RoomDatabaseKt")
        api = mockk()
        remoteDataSource = mockk()
        database = mockk()
        datastore = mockk()
        repository =
            GithubUserRepositoryImpl(remoteDataSource, database, datastore, dispatcherProvider)
        mediator = mockk()
    }

    @Test
    fun `getGithubUsersPaging return PagingData`() =
        runTest {
            // Given
            val userResponse =
                (0..20).map {
                    GithubUserResponse(it, "login$it", "avatar$it", "url$it")
                }
            val entities = userResponse.mapNotNull { it.toEntity() }
            val pagingSourceFactory = entities.asPagingSourceFactory()
            val pagingSource = pagingSourceFactory()
            val expectedPagingData = entities.map { it.toDomain() }
            coEvery {
                remoteDataSource.fetchUsers(any(), any())
            } returns userResponse
            coEvery { database.githubUserDao().insertUsers(any()) } just Runs
            coEvery { database.githubUserDao().clearAll() } just Runs
            coEvery { database.githubUserRemoteKeyDao().clearAll() } just Runs
            coEvery { database.githubUserRemoteKeyDao().insertAll(any()) } just Runs
            coEvery { database.githubUserRemoteKeyDao().getRemoteKeyByUserId(any()) } returns null
            coEvery { datastore.setLastUpdateTime(any()) } just Runs
            val transactionLambda = slot<suspend () -> Unit>()
            coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
                transactionLambda.captured.invoke()
            }

            coEvery { database.githubUserDao().getUserPagingSource() } returns pagingSource
            coEvery { datastore.lastUpdateTime } returns flow { emit(0) }

            // When
            val result = repository.getGithubUsersPaging().asSnapshot()

            // Then
            assertEquals(result, expectedPagingData)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getGithubUserDetail return GithubUserDetailResponse`() =
        runTest {
            // Given
            val username = "nhat"
            val response =
                GithubUserDetailResponse(
                    login = "nhat",
                    id = 1,
                    avatarUrl = "",
                    htmlUrl = "",
                    location = "Vietnam",
                    followers = 100,
                    following = 1,
                )
            val expectedDomain = response.toDomain()
            coEvery { remoteDataSource.fetchUserDetail(username) } returns response

            // When
            val result = repository.getGithubUserDetail(username).first()

            // Then
            assertEquals(expectedDomain, result)
        }
}
