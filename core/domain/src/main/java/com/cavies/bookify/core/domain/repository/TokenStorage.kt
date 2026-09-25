package com.cavies.bookify.core.domain.repository

interface TokenStorage {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun saveUser(id: Long, name: String, email: String, role: String)
    suspend fun getUserId(): Long
    suspend fun getUserName(): String
    suspend fun getUserEmail(): String
    suspend fun getUserRole(): String
    suspend fun clearAll()
    suspend fun isLoggedIn(): Boolean
}
