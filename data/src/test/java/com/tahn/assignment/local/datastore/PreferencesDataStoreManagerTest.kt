package com.tahn.assignment.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class PreferencesDataStoreManagerTest {
    private val testCoroutineDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

    private lateinit var preferencesDataStoreManager: PreferencesDataStoreManager
    private lateinit var dataStore: DataStore<Preferences>

    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testDataStoreFile: File = testContext.preferencesDataStoreFile("test_app_preferences")

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
        dataStore =
            PreferenceDataStoreFactory.create(
                scope = testCoroutineScope,
                produceFile = { testDataStoreFile },
            )
        preferencesDataStoreManager = PreferencesDataStoreManager(testContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDataStoreFile.delete()
    }

    @Test
    fun `setLastUpdateTime should store the timestamp in DataStore`() =
        testCoroutineScope.runTest {
            // Given
            val timestamp = System.currentTimeMillis()

            // When
            preferencesDataStoreManager.setLastUpdateTime(timestamp)

            // Then
            val actualTimestamp = preferencesDataStoreManager.lastUpdateTime.first()
            assertEquals(timestamp, actualTimestamp)
        }

    @Test
    fun `lastUpdateTime should return 0 when no timestamp is set`() =
        testCoroutineScope.runTest {
            // When
            val actualTimestamp = preferencesDataStoreManager.lastUpdateTime.first()

            // Then
            assertEquals(0L, actualTimestamp)
        }
}
