package com.tahn.assignment.di

import com.tahn.assignment.dispatcher.DispatcherProvider
import com.tahn.assignment.local.database.AppDatabase
import com.tahn.assignment.local.datastore.PreferencesDataStoreManager
import com.tahn.assignment.provider.DispatcherProviderImpl
import com.tahn.assignment.remote.api.NetworkBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val dispatcherModule =
    module {
        single<DispatcherProvider> { DispatcherProviderImpl() }
    }

private val remoteModule =
    module {
        single {
            NetworkBuilder.buildOkHttpClient(isDebug = true)
        }
        single {
            NetworkBuilder.buildService(get())
        }
    }

private val localModule =
    module {
        single {
            AppDatabase.getInstance(androidContext())
        }
        single {
            PreferencesDataStoreManager(androidContext())
        }
    }

val dataModules =
    module {
        includes(dispatcherModule, remoteModule, localModule)
    }
