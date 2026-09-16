package com.cavies.bookify.core.domain.model

data class AvailabilitySlot(
    val startAt: String,
    val endAt: String,
    val available: Boolean
)
