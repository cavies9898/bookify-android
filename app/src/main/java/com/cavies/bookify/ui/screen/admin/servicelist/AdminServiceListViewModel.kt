package com.cavies.bookify.ui.screen.admin.servicelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.usecase.DeleteServiceUseCase
import com.cavies.bookify.core.domain.usecase.GetServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminServiceListViewModel @Inject constructor(
    private val getServicesUseCase: GetServicesUseCase,
    private val deleteServiceUseCase: DeleteServiceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminServiceListUiState())
    val uiState: StateFlow<AdminServiceListUiState> = _uiState

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getServicesUseCase(page = 0, size = 100)
                .onSuccess { paginated ->
                    _uiState.update { it.copy(services = paginated.content, isLoading = false) }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun deleteService(id: Long) {
        viewModelScope.launch {
            deleteServiceUseCase(id)
                .onSuccess { loadServices() }
        }
    }
}
