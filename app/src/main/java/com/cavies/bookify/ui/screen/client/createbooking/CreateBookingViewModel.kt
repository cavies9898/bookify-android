package com.cavies.bookify.ui.screen.client.createbooking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.repository.TokenProvider
import com.cavies.bookify.core.domain.usecase.CreateBookingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateBookingViewModel @Inject constructor(
    private val createBookingUseCase: CreateBookingUseCase,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBookingUiState())
    val uiState: StateFlow<CreateBookingUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(userEmail = tokenProvider.getAccessToken()?.let { "" } ?: "") }
        }
    }

    fun createBooking(
        serviceId: Long,
        startAt: String,
        endAt: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            createBookingUseCase(serviceId, startAt, endAt)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
