package com.tahn.assignment.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PreferencesDataStoreManager(
    private val context: Context,
) {
    companion object {
        private const val DATA_STORE_NAME = "app_preferences"
        val LAST_UPDATE_TIME = longPreferencesKey("last_update_time")
    }

    private val Context.dataStore by preferencesDataStore(name = DATA_STORE_NAME)

    suspend fun setLastUpdateTime(timestamp: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_UPDATE_TIME] = timestamp
        }
    }

    val lastUpdateTime: Flow<Long> =
        context.dataStore.data
            .map { it[LAST_UPDATE_TIME] ?: 0 }
}
