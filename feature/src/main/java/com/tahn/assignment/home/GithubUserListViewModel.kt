package com.tahn.assignment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tahn.assignment.dispatcher.DispatcherProvider
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.usecase.GetGithubUsersPagingUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update

data class GithubUserListUiState(
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)

class GithubUserListViewModel(
    getGithubUsersPagingUseCase: GetGithubUsersPagingUseCase,
    dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    val userPagingSource: Flow<PagingData<GithubUser>> =
        getGithubUsersPagingUseCase()
            .cachedIn(viewModelScope)
            .flowOn(dispatcherProvider.io())

    private val _uiState = MutableStateFlow(GithubUserListUiState())
    val uiState: StateFlow<GithubUserListUiState> = _uiState.asStateFlow()

    fun setRefreshing(isLoading: Boolean) {
        _uiState.update { it.copy(isRefreshing = isLoading, errorMessage = null) }
    }

    fun setError(error: String) {
        _uiState.update { it.copy(isRefreshing = false, errorMessage = error) }
    }
}
