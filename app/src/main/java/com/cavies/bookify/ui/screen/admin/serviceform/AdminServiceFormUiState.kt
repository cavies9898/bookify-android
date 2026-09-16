package com.cavies.bookify.ui.screen.admin.serviceform

import com.cavies.bookify.core.domain.model.Service

data class AdminServiceFormUiState(
    val service: Service? = null,
    val name: String = "",
    val description: String = "",
    val durationMinutes: Int = 30,
    val capacity: Int = 1,
    val price: String = "",
    val openingTime: String = "09:00",
    val closingTime: String = "17:00",
    val location: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
