package com.tahn.assignment.detail

import androidx.lifecycle.SavedStateHandle
import com.tahn.assignment.dispatcher.DispatcherProvider
import com.tahn.assignment.model.GithubUserDetail
import com.tahn.assignment.usecase.GetGithubUserDetailUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GithubUserDetailViewModelTest {
    private lateinit var viewModel: GithubUserDetailViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private val getGithubUserDetailUseCase: GetGithubUserDetailUseCase = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val dispatcherProvider =
        object : DispatcherProvider {
            override fun io(): CoroutineDispatcher = testDispatcher

            override fun default(): CoroutineDispatcher = testDispatcher

            override fun main(): CoroutineDispatcher = testDispatcher

            override fun immediate(): CoroutineDispatcher = testDispatcher
        }

    private val mockGithubUserDetail =
        GithubUserDetail(
            id = 1,
            login = "octocat",
            avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
            htmlUrl = "https://github.com/octocat",
            location = "San Francisco",
            followers = 123,
            following = 456,
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf("username" to "octocat"))
        val dataFlow = flowOf(mockGithubUserDetail)
        every { getGithubUserDetailUseCase(any()) } returns dataFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `username_matches from SavedStateHandle`() {
        val viewModel =
            GithubUserDetailViewModel(
                getGithubUserDetailUseCase,
                dispatcherProvider,
                savedStateHandle,
            )
        assertEquals(viewModel.userDetail.username, "octocat")
    }
}
