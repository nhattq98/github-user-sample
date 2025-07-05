package com.tahn.assignment.remote

import com.tahn.assignment.model.remote.GithubUserDetailResponse
import com.tahn.assignment.model.remote.GithubUserResponse
import com.tahn.assignment.remote.api.GithubApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GithubRemoteDataSourceImplTest {
    private val api: GithubApiService = mockk()
    private lateinit var dataSource: GithubRemoteDataSourceImpl

    @Before
    fun setUp() {
        dataSource = GithubRemoteDataSourceImpl(api)
    }

    @Test
    fun `fetchUsers should return list of GithubUserResponse`() =
        runTest {
            // Given
            val perPage = 10
            val since = 20
            val expectedResponse =
                listOf(
                    GithubUserResponse(id = 1, login = "user1", avatarUrl = "url1", url = ""),
                )

            coEvery { api.getUsers(perPage, since) } returns expectedResponse

            // When
            val result = dataSource.fetchUsers(perPage, since)

            // Then
            assertEquals(expectedResponse, result)
            coVerify { api.getUsers(perPage, since) }
        }

    @Test
    fun `fetchUserDetail should return user detail`() =
        runTest {
            // Given
            val username = "johndoe"
            val expectedDetail =
                GithubUserDetailResponse(
                    login = "nhat",
                    id = 1,
                    avatarUrl = "",
                    htmlUrl = "",
                    location = "Vietnam",
                    followers = 100,
                    following = 1,
                )

            coEvery { api.getUserDetails(username) } returns expectedDetail

            // When
            val result = dataSource.fetchUserDetail(username)

            // Then
            assertEquals(expectedDetail, result)
            coVerify { api.getUserDetails(username) }
        }
}
