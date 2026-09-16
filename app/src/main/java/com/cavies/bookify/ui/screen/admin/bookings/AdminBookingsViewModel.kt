package com.cavies.bookify.ui.screen.admin.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.data.repository.BookingRepository
import com.cavies.bookify.core.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminBookingsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminBookingsUiState())
    val uiState: StateFlow<AdminBookingsUiState> = _uiState

    private var currentFilter: String? = null

    init {
        loadBookings()
        loadServiceNames()
    }

    fun setFilter(status: String?) {
        currentFilter = status
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            bookingRepository.getBookings(page = 0, status = currentFilter)
                .onSuccess { paginated ->
                    _uiState.update { it.copy(bookings = paginated.content, isLoading = false) }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            bookingRepository.getBookings(page = 0, status = currentFilter)
                .onSuccess { paginated ->
                    _uiState.update { it.copy(bookings = paginated.content, isRefreshing = false) }
                }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun loadServiceNames() {
        viewModelScope.launch {
            serviceRepository.getServices(page = 0, size = 100)
                .onSuccess { paginated ->
                    _uiState.update { it.copy(serviceNames = paginated.content.associate { s -> s.id to s.name }) }
                }
        }
    }
}
