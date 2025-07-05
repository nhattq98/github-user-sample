package com.tahn.assignment.utils

import android.util.Base64
import kotlin.experimental.xor

internal object SimpleTokenEncryptor {
    private const val SECRET: String = "tahn"

    fun encrypt(token: String): String {
        val xorBytes =
            token
                .toByteArray()
                .mapIndexed { i, byte ->
                    byte xor SECRET[i % SECRET.length].code.toByte()
                }.toByteArray()

        return Base64.encodeToString(xorBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedToken: String): String {
        val decodedBytes = Base64.decode(encryptedToken, Base64.DEFAULT)
        val originalBytes =
            decodedBytes
                .mapIndexed { i, byte ->
                    byte xor SECRET[i % SECRET.length].code.toByte()
                }.toByteArray()

        return String(originalBytes)
    }
}
