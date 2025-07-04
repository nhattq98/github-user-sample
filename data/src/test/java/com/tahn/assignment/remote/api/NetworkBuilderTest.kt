package com.tahn.assignment.remote.api

import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test

internal class NetworkBuilderTest {
    private lateinit var headerInterceptor: HeaderInterceptor

    @Before
    fun setup() {
        headerInterceptor = mockk(relaxed = true)
    }

    @Test
    fun `buildOkHttpClient should include HeaderInterceptor and logger - debug true`() {
        // Given
        val expectedLevel = HttpLoggingInterceptor.Level.BODY

        // When
        val okhttpClient = NetworkBuilder.buildOkHttpClient(headerInterceptor, true)

        // Then
        val interceptors = okhttpClient.interceptors
        val logging = interceptors.find { it is HttpLoggingInterceptor } as? HttpLoggingInterceptor

        assertTrue(interceptors.any { it is HttpLoggingInterceptor })
        assertTrue(interceptors.any { it == headerInterceptor })

        assertEquals(expectedLevel, logging?.level)
    }

    @Test
    fun `buildOkkHttpClient isDebug false should contain header and not logging interceptors`() {
        // Given
        val expectedLevel = HttpLoggingInterceptor.Level.NONE

        // When
        val client = NetworkBuilder.buildOkHttpClient(headerInterceptor, false)

        // Then
        val interceptors = client.interceptors
        val logging = interceptors.find { it is HttpLoggingInterceptor } as? HttpLoggingInterceptor

        assertTrue(interceptors.any { it is HttpLoggingInterceptor })
        assertTrue(interceptors.any { it == headerInterceptor })

        assertEquals(expectedLevel, logging?.level)
    }

    @Test
    fun `buildService should return retrofit service`() {
        val client = OkHttpClient.Builder().build()
        val service = NetworkBuilder.buildService(client)

        assertNotNull(service)
    }
}
