package com.cavies.bookify.ui.screen.client.servicedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceDetailUiState())
    val uiState: StateFlow<ServiceDetailUiState> = _uiState

    fun loadService(serviceId: Long) {
        viewModelScope.launch {
            serviceRepository.getServices(page = 0, size = 100)
                .onSuccess { paginated ->
                    _uiState.update { it.copy(service = paginated.content.find { s -> s.id == serviceId }) }
                }
        }
    }
}
