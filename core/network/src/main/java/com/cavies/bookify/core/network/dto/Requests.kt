package com.cavies.bookify.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class RegisterRequest(
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class ForgotPasswordRequest(
    @SerialName("email") val email: String
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refreshToken") val refreshToken: String
)

@Serializable
data class ResetPasswordRequest(
    @SerialName("token") val token: String,
    @SerialName("newPassword") val newPassword: String
)

@Serializable
data class CreateServiceRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("durationMinutes") val durationMinutes: Int,
    @SerialName("capacity") val capacity: Int,
    @SerialName("price") val price: Double,
    @SerialName("openingTime") val openingTime: String,
    @SerialName("closingTime") val closingTime: String,
    @SerialName("location") val location: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null
)

@Serializable
data class CreateBookingRequest(
    @SerialName("serviceId") val serviceId: Long,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String
)
