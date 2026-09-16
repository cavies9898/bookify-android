package com.cavies.bookify.ui.screen.admin.serviceform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.data.repository.ServiceRepository
import com.cavies.bookify.core.network.dto.CreateServiceRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminServiceFormViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminServiceFormUiState())
    val uiState: StateFlow<AdminServiceFormUiState> = _uiState

    fun updateName(value: String) = _uiState.update { it.copy(name = value) }
    fun updateDescription(value: String) = _uiState.update { it.copy(description = value) }
    fun updateDurationMinutes(value: Int) = _uiState.update { it.copy(durationMinutes = value) }
    fun updateCapacity(value: Int) = _uiState.update { it.copy(capacity = value) }
    fun updatePrice(value: String) = _uiState.update { it.copy(price = value) }
    fun updateOpeningTime(value: String) = _uiState.update { it.copy(openingTime = value) }
    fun updateClosingTime(value: String) = _uiState.update { it.copy(closingTime = value) }
    fun updateLocation(value: String) = _uiState.update { it.copy(location = value) }
    fun updateLatitude(value: String) = _uiState.update { it.copy(latitude = value) }
    fun updateLongitude(value: String) = _uiState.update { it.copy(longitude = value) }

    fun loadService(id: Long) {
        viewModelScope.launch {
            serviceRepository.getServices(page = 0, size = 100)
                .onSuccess { paginated ->
                    val svc = paginated.content.find { s -> s.id == id }
                    svc?.let {
                        _uiState.update { state ->
                            state.copy(
                                service = it,
                                name = it.name,
                                description = it.description ?: "",
                                durationMinutes = it.durationMinutes,
                                capacity = it.capacity,
                                price = if (it.price == 0.0) "" else it.price.toString(),
                                openingTime = it.openingTime ?: "09:00",
                                closingTime = it.closingTime ?: "17:00",
                                location = it.location ?: "",
                                latitude = it.latitude?.toString() ?: "",
                                longitude = it.longitude?.toString() ?: ""
                            )
                        }
                    }
                }
        }
    }

    fun saveService(
        id: Long?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = true, error = null) }
            val request = CreateServiceRequest(
                name = state.name,
                description = state.description,
                durationMinutes = state.durationMinutes,
                capacity = state.capacity,
                price = state.price.toDoubleOrNull() ?: 0.0,
                openingTime = state.openingTime,
                closingTime = state.closingTime,
                location = state.location.ifBlank { null },
                latitude = state.latitude.toDoubleOrNull(),
                longitude = state.longitude.toDoubleOrNull()
            )
            val result = if (id != null) {
                serviceRepository.updateService(id, request)
            } else {
                serviceRepository.createService(request)
            }
            result
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
