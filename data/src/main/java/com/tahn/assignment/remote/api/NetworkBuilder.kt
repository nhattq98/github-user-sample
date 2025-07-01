package com.tahn.assignment.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object NetworkBuilder {
    // Build UserServices with Retrofit
    fun buildService(okHttpClient: OkHttpClient): GithubApiService =
        Retrofit
            .Builder()
            .baseUrl("https://api.github.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApiService::class.java)
}
