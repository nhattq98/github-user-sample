package com.tahn.assignment

import android.app.Application
import com.tahn.assignment.di.dataModules
import com.tahn.assignment.di.useCaseModules
import com.tahn.assignment.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AssignmentApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@AssignmentApplication)
            modules(
                listOf(
                    dataModules,
                    useCaseModules,
                    viewModelModules,
                ),
            )
        }
    }
}
