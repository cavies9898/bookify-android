package com.cavies.bookify.ui.screen.admin.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.usecase.GetBookingsUseCase
import com.cavies.bookify.core.domain.usecase.GetServiceNamesUseCase
import com.cavies.bookify.ui.component.BookingFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminBookingsViewModel @Inject constructor(
    private val getBookingsUseCase: GetBookingsUseCase,
    private val getServiceNamesUseCase: GetServiceNamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminBookingsUiState())
    val uiState: StateFlow<AdminBookingsUiState> = _uiState

    init {
        loadBookings()
        loadServiceNames()
    }

    fun setFilter(filter: BookingFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        loadBookings()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            fetchBookings()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun loadBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            fetchBookings()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun fetchBookings() {
        val filter = _uiState.value.selectedFilter.status
        getBookingsUseCase(page = 0, status = filter)
            .onSuccess { paginated ->
                _uiState.update { it.copy(bookings = paginated.content) }
            }
    }

    private fun loadServiceNames() {
        viewModelScope.launch {
            getServiceNamesUseCase()
                .onSuccess { names ->
                    _uiState.update { it.copy(serviceNames = names) }
                }
        }
    }
}
