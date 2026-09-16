package com.cavies.bookify.core.network.dto

import com.cavies.bookify.core.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("tokenType") val tokenType: String,
    @SerialName("expiresIn") val expiresIn: Int,
    @SerialName("user") val user: UserDto?
) {
    fun toDomain() = com.cavies.bookify.core.domain.model.AuthResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresIn = expiresIn,
        user = user?.toDomain()
    )
}

@Serializable
data class UserDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("role") val role: String
) {
    fun toDomain() = User(
        id = id,
        name = name,
        email = email,
        role = com.cavies.bookify.core.domain.model.UserRole.valueOf(role)
    )
}

@Serializable
data class MessageResponse(
    @SerialName("message") val message: String
)
