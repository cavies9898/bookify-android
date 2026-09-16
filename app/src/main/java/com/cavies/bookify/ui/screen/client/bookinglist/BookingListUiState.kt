package com.cavies.bookify.ui.screen.client.bookinglist

import com.cavies.bookify.core.domain.model.Booking

data class BookingListUiState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val serviceNames: Map<Long, String> = emptyMap()
)
