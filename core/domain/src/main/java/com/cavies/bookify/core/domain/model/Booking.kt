package com.cavies.bookify.core.domain.model

data class Booking(
    val id: Long,
    val userId: Long?,
    val serviceId: Long,
    val serviceName: String?,
    val servicePrice: Double?,
    val startAt: String,
    val endAt: String,
    val status: BookingStatus,
    val createdAt: String?,
    val updatedAt: String?,
    val userName: String?,
    val userEmail: String?
)

enum class BookingStatus { PENDING, CONFIRMED, CANCELLED }
