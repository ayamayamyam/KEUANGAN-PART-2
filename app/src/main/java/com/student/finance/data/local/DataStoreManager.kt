package com.student.finance.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "student_finance_prefs")

@Singleton
class DataStoreManager @Inject constructor(private val context: Context) {

    private object Keys {
        val CURRENCY = stringPreferencesKey("currency")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val PIN_CODE = stringPreferencesKey("pin_code")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val currency: Flow<String> = context.dataStore.data.map { it[Keys.CURRENCY] ?: "IDR" }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_MODE] ?: false }
    val pinCode: Flow<String?> = context.dataStore.data.map { it[Keys.PIN_CODE] }
    val biometricEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.BIOMETRIC_ENABLED] ?: false }
    val appLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.APP_LOCK_ENABLED] ?: false }
    val userName: Flow<String> = context.dataStore.data.map { it[Keys.USER_NAME] ?: "Pengguna" }

    suspend fun setCurrency(value: String) {
        context.dataStore.edit { it[Keys.CURRENCY] = value }
    }

    suspend fun setDarkMode(value: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = value }
    }

    suspend fun setPinCode(value: String?) {
        context.dataStore.edit {
            if (value == null) it.remove(Keys.PIN_CODE) else it[Keys.PIN_CODE] = value
        }
    }

    suspend fun setBiometricEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.BIOMETRIC_ENABLED] = value }
    }

    suspend fun setAppLockEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.APP_LOCK_ENABLED] = value }
    }

    suspend fun setUserName(value: String) {
        context.dataStore.edit { it[Keys.USER_NAME] = value }
    }
}
