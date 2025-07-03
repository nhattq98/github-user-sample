package com.tahn.assignment.di

import com.tahn.assignment.usecase.GetGithubUserDetailUseCase
import com.tahn.assignment.usecase.GetGithubUsersPagingUseCase
import org.koin.dsl.module

val useCaseModules =
    module {
        single {
            GetGithubUsersPagingUseCase(get())
        }

        single {
            GetGithubUserDetailUseCase(get())
        }
    }
