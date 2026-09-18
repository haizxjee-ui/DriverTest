package com.example.drivertest.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "app_preferences"
)

class DataStoreManager(
    private val context: Context
) {

    private object Keys {
        val IS_DATA_INITIALIZED =
            booleanPreferencesKey("is_data_initialized")
    }

    val isDataInitialized: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[Keys.IS_DATA_INITIALIZED] ?: false
        }

    suspend fun setDataInitialized(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_DATA_INITIALIZED] = value
        }
    }
}