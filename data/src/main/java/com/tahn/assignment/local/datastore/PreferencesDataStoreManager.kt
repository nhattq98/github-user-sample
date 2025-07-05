package com.tahn.assignment.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tahn.assignment.local.datastore.PreferencesDataStoreManager.Companion.DATA_STORE_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.userPrefDataStore by preferencesDataStore(name = DATA_STORE_NAME)

internal class PreferencesDataStoreManager(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        const val DATA_STORE_NAME = "app_preferences"
        val LAST_UPDATE_TIME = longPreferencesKey("last_update_time")
    }

    suspend fun setLastUpdateTime(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_UPDATE_TIME] = timestamp
        }
    }

    val lastUpdateTime: Flow<Long> =
        dataStore.data
            .map { it[LAST_UPDATE_TIME] ?: 0 }
}
