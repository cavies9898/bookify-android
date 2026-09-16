package com.cavies.bookify.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bookify_prefs")

@Singleton
class TokenManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ID = longPreferencesKey("user_id")
    }

    private val encryptedPrefs by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "bookify_secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        encryptedPrefs.edit {
            putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
        }
    }

    suspend fun getAccessToken(): String? {
        return encryptedPrefs.getString("access_token", null)
    }

    suspend fun getRefreshToken(): String? {
        return encryptedPrefs.getString("refresh_token", null)
    }

    suspend fun saveUser(id: Long, name: String, email: String, role: String) {
        encryptedPrefs.edit {
            putLong("user_id", id)
                .putString("user_name", name)
                .putString("user_email", email)
                .putString("user_role", role)
        }
    }

    suspend fun getUserId(): Long = encryptedPrefs.getLong("user_id", 0L)
    suspend fun getUserName(): String = encryptedPrefs.getString("user_name", "") ?: ""
    suspend fun getUserEmail(): String = encryptedPrefs.getString("user_email", "") ?: ""
    suspend fun getUserRole(): String = encryptedPrefs.getString("user_role", "") ?: ""

    suspend fun clearAll() {
        encryptedPrefs.edit { clear() }
    }

    suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}
