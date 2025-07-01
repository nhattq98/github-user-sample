package com.tahn.assignment.di

import com.tahn.assignment.dispatcher.DispatcherProvider
import com.tahn.assignment.provider.DispatcherProviderImpl
import org.koin.dsl.module

private val dispatcherModule =
    module {
        single<DispatcherProvider> { DispatcherProviderImpl() }
    }

val dataModules =
    module {
        includes(dispatcherModule)
    }
