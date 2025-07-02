package com.tahn.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tahn.assignment.dispatcher.DispatcherProvider
import com.tahn.assignment.model.GithubUser
import com.tahn.assignment.usecase.GetGithubUsersPagingUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GithubUserListViewModel(
    getGithubUsersPagingUseCase: GetGithubUsersPagingUseCase,
    dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    val userPagingSource: Flow<PagingData<GithubUser>> =
        getGithubUsersPagingUseCase().cachedIn(viewModelScope).flowOn(dispatcherProvider.io())
}
