package com.tahn.assignment.di

import com.tahn.assignment.dispatcher.DispatcherProvider
import com.tahn.assignment.local.database.AppDatabase
import com.tahn.assignment.local.datastore.PreferencesDataStoreManager
import com.tahn.assignment.provider.DispatcherProviderImpl
import com.tahn.assignment.remote.GithubRemoteDataSource
import com.tahn.assignment.remote.GithubRemoteDataSourceImpl
import com.tahn.assignment.remote.api.HeaderInterceptor
import com.tahn.assignment.remote.api.NetworkBuilder
import com.tahn.assignment.repository.GithubUserRepository
import com.tahn.assignment.repository.GithubUserRepositoryImpl
import com.tahn.assignment.utils.FlavorUtils
import com.tahn.assignment.utils.SecureKeyManager
import com.tahn.assignment.utils.TokenManager
import com.tahn.assignment.utils.TokenManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val dispatcherModule =
    module {
        single<DispatcherProvider> { DispatcherProviderImpl() }
    }

private val remoteModule =
    module {
        single { SecureKeyManager() }
        single<TokenManager> {
            TokenManagerImpl(androidContext(), get())
        }
        single {
            HeaderInterceptor(get())
        }
        single {
            NetworkBuilder.buildOkHttpClient(
                headerInterceptor = get(),
                isDebug = FlavorUtils.isDebug,
            )
        }
        single {
            NetworkBuilder.buildService(get())
        }
        single<GithubRemoteDataSource> {
            GithubRemoteDataSourceImpl(get())
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

private val repositoryModule =
    module {
        single<GithubUserRepository> { GithubUserRepositoryImpl(get(), get(), get()) }
    }

val dataModules =
    module {
        includes(dispatcherModule, remoteModule, localModule, repositoryModule)
    }
