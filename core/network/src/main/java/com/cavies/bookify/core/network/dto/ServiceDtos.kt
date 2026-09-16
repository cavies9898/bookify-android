package com.cavies.bookify.core.network.dto

import com.cavies.bookify.core.domain.model.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("durationMinutes") val durationMinutes: Int,
    @SerialName("capacity") val capacity: Int,
    @SerialName("price") val price: Double,
    @SerialName("openingTime") val openingTime: String?,
    @SerialName("closingTime") val closingTime: String?,
    @SerialName("location") val location: String?,
    @SerialName("latitude") val latitude: Double?,
    @SerialName("longitude") val longitude: Double?,
    @SerialName("active") val active: Boolean?,
    @SerialName("createdAt") val createdAt: String?,
    @SerialName("updatedAt") val updatedAt: String?
) {
    fun toDomain() = Service(
        id = id,
        name = name,
        description = description,
        durationMinutes = durationMinutes,
        capacity = capacity,
        price = price,
        openingTime = openingTime,
        closingTime = closingTime,
        location = location,
        latitude = latitude,
        longitude = longitude,
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
