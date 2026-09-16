package com.cavies.bookify.core.data.repository

import android.util.Log
import com.cavies.bookify.core.data.local.TokenManager
import com.cavies.bookify.core.domain.model.AuthResponse
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
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val domain = response.toDomain()
            tokenManager.saveTokens(domain.accessToken, domain.refreshToken)
            domain.user?.let {
                tokenManager.saveUser(it.id, it.name, it.email, it.role.name)
            } ?: tokenManager.saveUser(0L, "", email, "CLIENTE")
            Result.success(domain)
        } catch (e: retrofit2.HttpException) {
            val message = when (e.code()) {
                401 -> "Credenciales incorrectas"
                403 -> "No tienes permiso"
                404 -> "Recurso no encontrado"
                422 -> "Datos inválidos"
                else -> "Error del servidor"
            }
            Result.failure(Exception(message))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
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
            tokenManager.saveTokens(domain.accessToken, domain.refreshToken)
            domain.user?.let {
                tokenManager.saveUser(it.id, it.name, it.email, it.role.name)
            }
            Result.success(domain)
        } catch (e: retrofit2.HttpException) {
            val message = when (e.code()) {
                401 -> "Credenciales incorrectas"
                403 -> "No tienes permiso"
                404 -> "Recurso no encontrado"
                422 -> "Datos inválidos"
                else -> "Error del servidor"
            }
            Result.failure(Exception(message))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            Result.success(response.message)
        } catch (e: retrofit2.HttpException) {
            val message = when (e.code()) {
                404 -> "Email no registrado"
                422 -> "Email inválido"
                else -> "Error del servidor"
            }
            Result.failure(Exception(message))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<String> {
        return try {
            val response = api.resetPassword(ResetPasswordRequest(token, newPassword))
            Result.success(response.message)
        } catch (e: retrofit2.HttpException) {
            val message = when (e.code()) {
                400 -> "Código de restablecimiento inválido"
                404 -> "Token no encontrado"
                422 -> "Datos inválidos"
                else -> "Error del servidor"
            }
            Result.failure(Exception(message))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    override suspend fun logout() {
        tokenManager.clearAll()
    }

    override suspend fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    override suspend fun getCurrentUser(): com.cavies.bookify.core.domain.model.User? {
        if (!isLoggedIn()) return null
        return com.cavies.bookify.core.domain.model.User(
            id = tokenManager.getUserId(),
            name = tokenManager.getUserName(),
            email = tokenManager.getUserEmail(),
            role = com.cavies.bookify.core.domain.model.UserRole.valueOf(
                tokenManager.getUserRole().ifEmpty { "CLIENTE" }
            )
        )
    }
}
