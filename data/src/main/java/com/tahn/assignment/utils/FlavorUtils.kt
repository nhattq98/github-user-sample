package com.tahn.assignment.utils

import com.tahn.assignment.BuildConfig

internal object FlavorUtils {
    val isDev: Boolean get() = BuildConfig.FLAVOR == "dev"
    val isProd: Boolean get() = BuildConfig.FLAVOR == "prod"

    val isDebug: Boolean get() = BuildConfig.BUILD_TYPE == "debug"
    val isRelease: Boolean get() = BuildConfig.BUILD_TYPE == "release"

    val fullMode: String get() = "${BuildConfig.FLAVOR}_${BuildConfig.BUILD_TYPE}"
}
