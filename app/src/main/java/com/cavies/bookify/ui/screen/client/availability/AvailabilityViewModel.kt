package com.cavies.bookify.ui.screen.client.availability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.usecase.GetServiceAvailabilityUseCase
import com.cavies.bookify.core.domain.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AvailabilityViewModel @Inject constructor(
    private val getServiceAvailabilityUseCase: GetServiceAvailabilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvailabilityUiState())
    val uiState: StateFlow<AvailabilityUiState> = _uiState

    fun loadAvailability(serviceId: Long, date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val dateStr = DateUtils.toQueryString(date)
            getServiceAvailabilityUseCase(serviceId, dateStr)
                .onSuccess {
                    _uiState.update { state -> state.copy(slots = it, isLoading = false, error = null) }
                }
                .onFailure { e ->
                    _uiState.update { state ->
                        state.copy(slots = emptyList(), isLoading = false, error = e.message ?: "Error al cargar disponibilidad")
                    }
                }
        }
    }
}
