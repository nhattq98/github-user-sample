package com.tahn.assignment.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface SecureKeyManager {
    fun encrypt(data: String): String

    fun decrypt(encrypt: String): String
}

class SecureKeyManagerImpl : SecureKeyManager {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
    private val keyAlias = "TokenEncryptionKey"

    init {
        keyStore.load(null)
        generateOrGetKey()
    }

    private fun generateOrGetKey() {
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec =
                KeyGenParameterSpec
                    .Builder(
                        keyAlias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setUserAuthenticationRequired(false)
                    .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey = keyStore.getKey(keyAlias, null) as SecretKey

    override fun encrypt(data: String): String =
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

            val iv = cipher.iv
            val encryptedData = cipher.doFinal(data.toByteArray())

            // Combine IV and encrypted data
            val combined = iv + encryptedData
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (_: Exception) {
            ""
        }

    override fun decrypt(encryptedData: String): String =
        try {
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)

            // Extract IV (first 12 bytes for GCM)
            val iv = combined.sliceArray(0..11)
            val encrypted = combined.sliceArray(12 until combined.size)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmParameterSpec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), gcmParameterSpec)

            val decryptedData = cipher.doFinal(encrypted)
            String(decryptedData)
        } catch (_: Exception) {
            ""
        }
}
