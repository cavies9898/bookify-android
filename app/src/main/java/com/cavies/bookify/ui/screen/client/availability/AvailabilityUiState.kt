package com.cavies.bookify.ui.screen.client.availability

import com.cavies.bookify.core.domain.model.AvailabilitySlot
import java.time.LocalDate

data class AvailabilityUiState(
    val slots: List<AvailabilitySlot> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val error: String? = null
)
