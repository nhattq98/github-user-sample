package com.tahn.assignment.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal interface TokenManager {
    fun getMockAccessToken(): String
}

private val Context.dataStore by preferencesDataStore("secure_prefs")

internal class TokenManagerImpl(
    private val context: Context,
    private val keyStoreManager: SecureKeyManager,
) : TokenManager {
    fun mockAccessToken() = SimpleTokenEncryptor.decrypt(AccessTokenMock.MOCK)

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("encrypted_token")
    }

    override fun getMockAccessToken() = mockAccessToken()

    // Save encrypt token to data store
    suspend fun saveToken(token: String) {
        val encryptedToken = keyStoreManager.encrypt(token)
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = encryptedToken
        }
    }

    suspend fun getToken(): String? {
        val encryptedToken =
            context.dataStore.data
                .map { it[TOKEN_KEY] }
                .first()

        return encryptedToken?.let { keyStoreManager.decrypt(it) }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}

// cause we don't access to github by login flow
// this class mock access token
// decrypt by SimpleTokenEncryptor
// the token valid for 30 days
object AccessTokenMock {
    const val MOCK = "EwkYMUMEW10GDi8vHAkcKEQyEjoNUVBfByofOxo4CiQuEFk7RTESPQ==\n"
}
