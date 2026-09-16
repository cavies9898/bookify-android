package com.cavies.bookify.ui.screen.client.bookingdetail

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
class BookingDetailViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingDetailUiState())
    val uiState: StateFlow<BookingDetailUiState> = _uiState

    fun loadBooking(bookingId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            bookingRepository.getBookings(page = 0, size = 100)
                .onSuccess { paginated ->
                    val found = paginated.content.find { it.id == bookingId }
                    _uiState.update { it.copy(booking = found) }
                    found?.let {
                        serviceRepository.getServices(page = 0, size = 100)
                            .onSuccess { services ->
                                val service = services.content.find { s -> s.id == it.serviceId }
                                _uiState.update { state ->
                                    state.copy(
                                        serviceName = service?.name ?: "Servicio",
                                        serviceDescription = service?.description ?: "",
                                        serviceLocation = service?.location,
                                        serviceLatitude = service?.latitude,
                                        serviceLongitude = service?.longitude,
                                        isLoading = false
                                    )
                                }
                            }
                    }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun cancelBooking(bookingId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            bookingRepository.cancelBooking(bookingId)
                .onSuccess {
                    loadBooking(bookingId)
                    onDone()
                }
        }
    }
}
