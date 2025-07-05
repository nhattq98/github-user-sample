package com.tahn.assignment.usecase

import com.tahn.assignment.model.GithubUserDetail
import com.tahn.assignment.repository.GithubUserRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

internal class GetGithubUserDetailUseCaseTest {
    private lateinit var repository: GithubUserRepository
    private lateinit var getGithubUserDetailUseCase: GetGithubUserDetailUseCase

    @Before
    fun setup() {
        repository = mockk()
        getGithubUserDetailUseCase = GetGithubUserDetailUseCase(repository)
    }

    @Test
    fun `invoke call should return GithubUserDetail`() =
        runTest {
            // Given
            val username = "nhattq"
            val userDetails = mockk<GithubUserDetail>()

            every { repository.getGithubUserDetail(username) } returns flowOf(userDetails)

            // When
            val result = getGithubUserDetailUseCase(username).first()

            // Then
            assertEquals(result, result)
        }
}
