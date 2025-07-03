package com.tahn.assignment.di

import androidx.lifecycle.SavedStateHandle
import com.tahn.assignment.detail.GithubUserDetailViewModel
import com.tahn.assignment.home.GithubUserListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModules =
    module {
        viewModel { GithubUserListViewModel(get(), get()) }
        viewModel { (savedStateHandle: SavedStateHandle) ->
            GithubUserDetailViewModel(
                getGithubUserDetailUserDetailUseCase = get(),
                dispatcherProvider = get(),
                savedStateHandle = savedStateHandle,
            )
        }
    }
