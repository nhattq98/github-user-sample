package com.tahn.assignment.usecase

import androidx.paging.PagingData
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.repository.GithubUserRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

internal class GetGithubUserPagingUseCaseTest {
    private lateinit var repository: GithubUserRepository
    private lateinit var getUserPagingSourceUseCase: GetGithubUsersPagingUseCase

    @Before
    fun setup() {
        repository = mockk()
        getUserPagingSourceUseCase = GetGithubUsersPagingUseCase(repository)
    }

    @Test
    fun `invoke call should return PagingSourceData`() =
        runTest {
            // Given
            val userPagingData =
                PagingData.from(
                    listOf(
                        GithubUser(id = 1, username = "nhat", avatarUrl = "", profileUrl = ""),
                    ),
                )

            every { repository.getGithubUsersPaging() } returns flowOf(userPagingData)

            // When
            val result = getUserPagingSourceUseCase().first()

            // Then
            assertEquals(userPagingData, result)
        }
}
