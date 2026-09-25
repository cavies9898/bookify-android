package com.cavies.bookify.ui.screen.client.servicedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.usecase.GetServiceByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    private val getServiceByIdUseCase: GetServiceByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceDetailUiState())
    val uiState: StateFlow<ServiceDetailUiState> = _uiState

    fun loadService(serviceId: Long) {
        viewModelScope.launch {
            getServiceByIdUseCase(serviceId)
                .onSuccess { service ->
                    _uiState.update { it.copy(service = service) }
                }
        }
    }
}
