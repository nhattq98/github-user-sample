package com.tahn.assignment.remote.api

import com.tahn.assignment.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class HeaderInterceptor(
    private val tokenManager: TokenManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val modifiedRequest =
            originalRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .method(originalRequest.method, originalRequest.body)

        modifiedRequest.addHeader(
            AUTHORIZATION,
            "Bearer ${tokenManager.getMockAccessToken()}",
        )

        return chain.proceed(modifiedRequest.build())
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
    }
}
