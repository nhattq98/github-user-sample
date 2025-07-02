package com.tahn.assignment

import android.app.Application
import com.tahn.assignment.di.dataModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AssignmentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@AssignmentApplication)
            modules(
                listOf(
                    dataModules,
                ),
            )
        }
    }
}
