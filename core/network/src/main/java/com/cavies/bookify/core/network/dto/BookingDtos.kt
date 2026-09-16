package com.cavies.bookify.core.network.dto

import com.cavies.bookify.core.domain.model.Booking
import com.cavies.bookify.core.domain.model.BookingStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingDto(
    @SerialName("id") val id: Long,
    @SerialName("status") val status: String,
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("createdAt") val createdAt: String?,
    @SerialName("updatedAt") val updatedAt: String?,
    @SerialName("service") val service: BookingServiceDto?,
    @SerialName("userName") val userName: String?,
    @SerialName("userEmail") val userEmail: String?
) {
    fun toDomain() = Booking(
        id = id,
        userId = null,
        serviceId = service?.id ?: 0L,
        serviceName = service?.name,
        servicePrice = service?.price,
        startAt = startAt,
        endAt = endAt,
        status = BookingStatus.valueOf(status),
        createdAt = createdAt,
        updatedAt = updatedAt,
        userName = userName,
        userEmail = userEmail
    )
}

@Serializable
data class BookingServiceDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("price") val price: Double?
)
