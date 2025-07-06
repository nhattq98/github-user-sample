package com.tahn.assignment.home

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.tahn.assignment.dispatcher.DispatcherProvider
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.usecase.GetGithubUsersPagingUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GithubUserListViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider =
        object : DispatcherProvider {
            override fun io() = testDispatcher

            override fun default() = testDispatcher

            override fun main() = testDispatcher

            override fun immediate() = testDispatcher
        }

    private val getGithubUsersPagingUseCase = mockk<GetGithubUsersPagingUseCase>()

    private lateinit var viewModel: GithubUserListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        val expectedUsers =
            listOf(
                GithubUser(1, "octocat", "https://avatar.url", ""),
                GithubUser(2, "hubcat", "https://avatar2.url", ""),
            )
        val pagingData = PagingData.from(expectedUsers)
        every { getGithubUsersPagingUseCase() } returns flowOf(pagingData) // before viewModel
        viewModel = GithubUserListViewModel(getGithubUsersPagingUseCase, dispatcherProvider)
    }

    @Test
    fun `initial state should have isRefreshing false and errorMessage null`() =
        runTest {
            val state = viewModel.uiState.value
            assertFalse(state.isRefreshing)
            assertNull(state.errorMessage)
        }

    @Test
    fun `setRefreshing true should update isRefreshing and clear errorMessage`() =
        runTest {
            viewModel = GithubUserListViewModel(getGithubUsersPagingUseCase, dispatcherProvider)

            viewModel.setError("Some error")
            viewModel.setRefreshing(true)

            val state = viewModel.uiState.value
            assertTrue(state.isRefreshing)
            assertNull(state.errorMessage)
        }

    @Test
    fun `setError should update errorMessage and reset isRefreshing`() =
        runTest {
            viewModel.setRefreshing(true)
            viewModel.setError("Network error")

            val state = viewModel.uiState.value
            assertEquals("Network error", state.errorMessage)
            assertFalse(state.isRefreshing)
        }

    @Test
    fun `userPagingSource emits expected paging data`() =
        runTest {
            // Given
            val differ =
                AsyncPagingDataDiffer(
                    diffCallback = DIFF_CALLBACK,
                    updateCallback = NoopListCallback(),
                    workerDispatcher = testDispatcher,
                    mainDispatcher = testDispatcher,
                )

            // When
            val job =
                launch {
                    viewModel.userPagingSource.collectLatest { differ.submitData(it) }
                }

            advanceUntilIdle()

            // Then
            assertEquals(2, differ.snapshot().size)
            assertEquals("octocat", differ.snapshot()[0]?.username)

            job.cancel()
        }

    private object DIFF_CALLBACK : DiffUtil.ItemCallback<GithubUser>() {
        override fun areItemsTheSame(
            oldItem: GithubUser,
            newItem: GithubUser,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: GithubUser,
            newItem: GithubUser,
        ): Boolean = oldItem == newItem
    }

    private class NoopListCallback : ListUpdateCallback {
        override fun onInserted(
            position: Int,
            count: Int,
        ) {
        }

        override fun onRemoved(
            position: Int,
            count: Int,
        ) {
        }

        override fun onMoved(
            fromPosition: Int,
            toPosition: Int,
        ) {
        }

        override fun onChanged(
            position: Int,
            count: Int,
            payload: Any?,
        ) {
        }
    }
}
