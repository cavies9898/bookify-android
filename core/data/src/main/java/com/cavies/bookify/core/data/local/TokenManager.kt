package com.cavies.bookify.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.cavies.bookify.core.domain.repository.TokenStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bookify_prefs")

@Singleton
class TokenManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) : TokenStorage {

    private val encryptedPrefs by lazy {
        try {
            createEncryptedPrefs()
        } catch (_: Exception) {
            context.deleteSharedPreferences("bookify_secure_prefs")
            createEncryptedPrefs()
        }
    }

    private fun createEncryptedPrefs() = EncryptedSharedPreferences.create(
        context,
        "bookify_secure_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        encryptedPrefs.edit {
            putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
        }
    }

    override suspend fun getAccessToken(): String? {
        return encryptedPrefs.getString("access_token", null)
    }

    override suspend fun getRefreshToken(): String? {
        return encryptedPrefs.getString("refresh_token", null)
    }

    override suspend fun saveUser(id: Long, name: String, email: String, role: String) {
        encryptedPrefs.edit {
            putLong("user_id", id)
                .putString("user_name", name)
                .putString("user_email", email)
                .putString("user_role", role)
        }
    }

    override suspend fun getUserId(): Long = encryptedPrefs.getLong("user_id", 0L)
    override suspend fun getUserName(): String = encryptedPrefs.getString("user_name", "") ?: ""
    override suspend fun getUserEmail(): String = encryptedPrefs.getString("user_email", "") ?: ""
    override suspend fun getUserRole(): String = encryptedPrefs.getString("user_role", "") ?: ""

    override suspend fun clearAll() {
        encryptedPrefs.edit { clear() }
    }

    override suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}
