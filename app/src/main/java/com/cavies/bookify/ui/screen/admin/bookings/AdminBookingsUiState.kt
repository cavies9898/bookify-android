package com.cavies.bookify.ui.screen.admin.bookings

import com.cavies.bookify.core.domain.model.Booking

data class AdminBookingsUiState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val serviceNames: Map<Long, String> = emptyMap()
)
