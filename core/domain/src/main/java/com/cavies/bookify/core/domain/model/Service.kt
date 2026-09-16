package com.cavies.bookify.core.domain.model

data class Service(
    val id: Long,
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val capacity: Int,
    val price: Double,
    val openingTime: String?,
    val closingTime: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val active: Boolean?,
    val createdAt: String?,
    val updatedAt: String?
)
