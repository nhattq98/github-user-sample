package com.tahn.assignment.remote.api

import com.tahn.assignment.utils.TokenManager
import io.mockk.coEvery
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HeaderInterceptorTest {
    private lateinit var tokenManager: TokenManager
    private lateinit var interceptor: HeaderInterceptor

    @Before
    fun setup() {
        tokenManager = mockk()
        interceptor = HeaderInterceptor(tokenManager)
    }

    @Test
    fun `interceptor adds Authorization header`() {
        val expectedToken = "mocked-token"
        coEvery { tokenManager.getAccessToken() } returns expectedToken

        val request =
            Request
                .Builder()
                .url("https://api.github.com")
                .get()
                .build()

        val chain = mockk<Interceptor.Chain>()
        val capturedRequestSlot = mutableListOf<Request>()

        val fakeResponse =
            Response
                .Builder()
                .code(200)
                .message("OK")
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .request(request)
                .body("{}".toResponseBody())
                .build()

        // Capture
        coEvery { chain.request() } returns request
        coEvery { chain.proceed(capture(capturedRequestSlot)) } returns fakeResponse

        // Act
        interceptor.intercept(chain)

        // Assert
        val modifiedRequest = capturedRequestSlot.first()
        assertEquals("Bearer $expectedToken", modifiedRequest.header("Authorization"))
        assertEquals("application/json", modifiedRequest.header("Content-Type"))
    }
}
