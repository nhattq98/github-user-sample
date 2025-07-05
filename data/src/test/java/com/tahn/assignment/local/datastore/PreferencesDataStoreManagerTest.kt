package com.tahn.assignment.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
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
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class PreferencesDataStoreManagerTest {
    private lateinit var temporaryFolder: TemporaryFolder
    private val testCoroutineDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

    private lateinit var preferencesDataStoreManager: PreferencesDataStoreManager
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var testDataStoreFile: File

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
        temporaryFolder = TemporaryFolder()
        temporaryFolder.create()
        dataStore =
            PreferenceDataStoreFactory.create(
                scope = testCoroutineScope,
                produceFile = { temporaryFolder.newFile("test_prefs.preferences_pb") },
            )
        preferencesDataStoreManager = PreferencesDataStoreManager(dataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
//        testDataStoreFile.delete()
    }

    @Test
    fun `lastUpdateTime should return 0 when no timestamp is set`() =
        testCoroutineScope.runTest {
            // When
            val actualTimestamp = preferencesDataStoreManager.lastUpdateTime.first()

            // Then
            assertEquals(0L, actualTimestamp)
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
}
