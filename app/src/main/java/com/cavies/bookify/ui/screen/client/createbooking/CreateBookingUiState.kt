package com.cavies.bookify.ui.screen.client.createbooking

data class CreateBookingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userEmail: String = ""
)
