package com.example.drivertest.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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

        val SELECTED_LICENSE_TYPE_ID =
            intPreferencesKey("selected_license_type_id")
    }

    val isDataInitialized: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[Keys.IS_DATA_INITIALIZED] ?: false
        }

    val selectedLicenseTypeID: Flow<Int?> =
        context.dataStore.data.map { preferences ->
            preferences[Keys.SELECTED_LICENSE_TYPE_ID]
        }

    suspend fun setDataInitialized(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_DATA_INITIALIZED] = value
        }
    }

    suspend fun setSelectedLicenseTypeID(licenseTypeID: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SELECTED_LICENSE_TYPE_ID] = licenseTypeID
        }
    }
}