package com.cavies.bookify.ui.screen.admin.servicelist

import com.cavies.bookify.core.domain.model.Service

data class AdminServiceListUiState(
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = false
)
