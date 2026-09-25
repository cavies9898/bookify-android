package com.cavies.bookify.ui.screen.client.bookinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.usecase.GetBookingsUseCase
import com.cavies.bookify.core.domain.usecase.GetServiceNamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingListViewModel @Inject constructor(
    private val getBookingsUseCase: GetBookingsUseCase,
    private val getServiceNamesUseCase: GetServiceNamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingListUiState())
    val uiState: StateFlow<BookingListUiState> = _uiState

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
            getBookingsUseCase(page = 0, status = currentFilter)
                .onSuccess { paginated ->
                    _uiState.update { it.copy(bookings = paginated.content, isLoading = false) }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            getBookingsUseCase(page = 0, status = currentFilter)
                .onSuccess { paginated ->
                    _uiState.update { it.copy(bookings = paginated.content, isRefreshing = false) }
                }
            _uiState.update { it.copy(isRefreshing = false) }
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
