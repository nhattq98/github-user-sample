package com.tahn.assignment.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tahn.assignment.dispatcher.DispatcherProvider
import com.tahn.assignment.model.GithubUserDetail
import com.tahn.assignment.nav.UserDetail
import com.tahn.assignment.result.Result
import com.tahn.assignment.result.asResult
import com.tahn.assignment.usecase.GetGithubUserDetailUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GithubUserDetailUiState(
    val userDetail: GithubUserDetail? = null,
    val isLoading: Boolean = false,
)

class GithubUserDetailViewModel(
    val getGithubUserDetailUserDetailUseCase: GetGithubUserDetailUseCase,
    dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _userDetail: UserDetail = savedStateHandle.toRoute<UserDetail>()
    val userDetail: UserDetail
        get() = _userDetail

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage

    val githubUserDetailUiState: StateFlow<GithubUserDetailUiState> =
        getGithubUserDetailUserDetailUseCase(_userDetail.username)
            .asResult()
            .map(::toGithubUserDetailUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GithubUserDetailUiState(isLoading = true),
            )

    private fun toGithubUserDetailUiState(result: Result<GithubUserDetail>): GithubUserDetailUiState =
        when (result) {
            is Result.Loading -> GithubUserDetailUiState(isLoading = true)

            is Result.Success ->
                GithubUserDetailUiState(
                    userDetail = result.data,
                    isLoading = false,
                )

            is Result.Error -> {
                showError(result.exception.message.toString())
                GithubUserDetailUiState(
                    isLoading = false,
                )
            }
        }

    private fun showError(message: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }
}
