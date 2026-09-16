package com.cavies.bookify.ui.screen.client.servicelist

import com.cavies.bookify.core.domain.model.Service

data class ClientServiceListUiState(
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null
)
