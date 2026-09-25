package com.cavies.bookify.core.data.repository

import com.cavies.bookify.core.domain.model.AuthResponse
import com.cavies.bookify.core.domain.model.User
import com.cavies.bookify.core.domain.model.UserRole
import com.cavies.bookify.core.domain.repository.AuthRepository
import com.cavies.bookify.core.domain.repository.TokenStorage
import com.cavies.bookify.core.data.util.ErrorMapper
import com.cavies.bookify.core.network.api.ApiService
import com.cavies.bookify.core.network.dto.ForgotPasswordRequest
import com.cavies.bookify.core.network.dto.LoginRequest
import com.cavies.bookify.core.network.dto.RegisterRequest
import com.cavies.bookify.core.network.dto.ResetPasswordRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val domain = response.toDomain()
            tokenStorage.saveTokens(domain.accessToken, domain.refreshToken)
            domain.user?.let {
                tokenStorage.saveUser(it.id, it.name, it.email, it.role.name)
            } ?: tokenStorage.saveUser(0L, "", email, "CLIENTE")
            Result.success(domain)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMapper.mapException(e)))
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(name, email, password))
            val domain = response.toDomain()
            tokenStorage.saveTokens(domain.accessToken, domain.refreshToken)
            domain.user?.let {
                tokenStorage.saveUser(it.id, it.name, it.email, it.role.name)
            }
            Result.success(domain)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMapper.mapException(e)))
        }
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMapper.mapException(e)))
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<String> {
        return try {
            val response = api.resetPassword(ResetPasswordRequest(token, newPassword))
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorMapper.mapException(e)))
        }
    }

    override suspend fun logout() {
        tokenStorage.clearAll()
    }

    override suspend fun isLoggedIn(): Boolean = tokenStorage.isLoggedIn()

    override suspend fun getCurrentUser(): User? {
        if (!isLoggedIn()) return null
        return User(
            id = tokenStorage.getUserId(),
            name = tokenStorage.getUserName(),
            email = tokenStorage.getUserEmail(),
            role = UserRole.valueOf(tokenStorage.getUserRole().ifEmpty { "CLIENTE" })
        )
    }
}
