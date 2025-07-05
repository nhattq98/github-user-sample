package com.tahn.assignment.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class TokenManagerImplTest {
    private lateinit var context: Context
    private lateinit var tokenManager: TokenManagerImpl
    private val mockSecureKeyManager = mockk<SecureKeyManager>()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        tokenManager = TokenManagerImpl(context, mockSecureKeyManager)
        runTest { tokenManager.clearToken() } // Clear before each test
    }

    @After
    fun tearDown() {
        runTest { tokenManager.clearToken() }
    }

    @Test
    fun `getAccessToken should return decrypted token when token is saved`() =
        runTest {
            // Given
            val plain = "cached_token"
            val encrypted = "encrypted_cached"
            coEvery { mockSecureKeyManager.encrypt(plain) } returns encrypted
            coEvery { mockSecureKeyManager.decrypt(encrypted) } returns plain

            // When
            tokenManager.saveToken(plain)

            // Then
            val result = tokenManager.getAccessToken()
            assertEquals(plain, result)
        }

    @Test
    fun `clearToken should remove saved token`() =
        runTest {
            val token = "to_clear"
            val encrypted = "enc_to_clear"

            coEvery { mockSecureKeyManager.encrypt(token) } returns encrypted
            coEvery { mockSecureKeyManager.decrypt(encrypted) } returns token

            tokenManager.saveToken(token)
            tokenManager.clearToken()

            val result = tokenManager.getToken()
            assertNull(result)
        }
}
