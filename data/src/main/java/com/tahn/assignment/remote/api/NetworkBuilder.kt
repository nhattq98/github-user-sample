package com.tahn.assignment.remote.api

import com.tahn.assignment.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal object NetworkBuilder {
    private const val TIME_OUT = 60L

    fun buildOkHttpClient(
        headerInterceptor: HeaderInterceptor,
        isDebug: Boolean,
    ): OkHttpClient {
        val loggerInterceptor =
            HttpLoggingInterceptor().apply {
                if (isDebug) {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    setLevel(HttpLoggingInterceptor.Level.NONE)
                }
            }

        return OkHttpClient
            .Builder()
            .addInterceptor(loggerInterceptor)
            .addInterceptor(headerInterceptor)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    fun buildService(okHttpClient: OkHttpClient): GithubApiService =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApiService::class.java)
}
