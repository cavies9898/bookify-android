package com.cavies.bookify.core.domain.repository

import com.cavies.bookify.core.domain.model.AuthResponse
import com.cavies.bookify.core.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResponse>
    suspend fun register(name: String, email: String, password: String): Result<AuthResponse>
    suspend fun forgotPassword(email: String): Result<String>
    suspend fun resetPassword(token: String, newPassword: String): Result<String>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): User?
}
