package com.cavies.bookify.ui.screen.client.bookingdetail

import com.cavies.bookify.core.domain.model.Booking

data class BookingDetailUiState(
    val booking: Booking? = null,
    val serviceName: String = "",
    val serviceDescription: String = "",
    val serviceLocation: String? = null,
    val serviceLatitude: Double? = null,
    val serviceLongitude: Double? = null,
    val isLoading: Boolean = false
)
