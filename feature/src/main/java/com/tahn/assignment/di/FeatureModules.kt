package com.tahn.assignment.di

import com.tahn.assignment.home.GithubUserListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModules =
    module {
        viewModel { GithubUserListViewModel(get(), get()) }
    }
