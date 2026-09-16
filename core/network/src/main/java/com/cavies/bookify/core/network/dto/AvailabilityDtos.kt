package com.cavies.bookify.core.network.dto

import com.cavies.bookify.core.domain.model.AvailabilitySlot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailabilityResponseDto(
    @SerialName("serviceId") val serviceId: Long,
    @SerialName("date") val date: String,
    @SerialName("slots") val slots: List<AvailabilitySlotDto>
)

@Serializable
data class AvailabilitySlotDto(
    @SerialName("startAt") val startAt: String,
    @SerialName("endAt") val endAt: String,
    @SerialName("available") val available: Boolean
) {
    fun toDomain() = AvailabilitySlot(
        startAt = startAt,
        endAt = endAt,
        available = available
    )
}
