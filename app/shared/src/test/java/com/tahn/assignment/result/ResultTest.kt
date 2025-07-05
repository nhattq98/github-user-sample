package com.tahn.assignment.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AsResultTest {
    @Test
    fun `asResult should emit Loading then Success`() =
        runTest {
            // Given
            val testFlow: Flow<String> =
                flow {
                    emit("Hello")
                }

            // When
            val result = testFlow.asResult().toList()

            // Then
            assertEquals(2, result.size)
            assertTrue(result[0] is Result.Loading)
            assertTrue(result[1] is Result.Success)
            assertEquals("Hello", (result[1] as Result.Success).data)
        }

    @Test
    fun `asResult should emit Loading then Error when exception is thrown`() =
        runTest {
            // Given
            val exception = IllegalStateException("Something went wrong")
            val testFlow: Flow<String> =
                flow {
                    throw exception
                }

            // When
            val result = testFlow.asResult().toList()

            // Then
            assertEquals(2, result.size)
            assertTrue(result[0] is Result.Loading)
            assertTrue(result[1] is Result.Error)
        }
}
