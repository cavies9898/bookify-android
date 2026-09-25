package com.cavies.bookify.ui.screen.client.servicelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.usecase.GetServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientServiceListViewModel @Inject constructor(
    private val getServicesUseCase: GetServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientServiceListUiState())
    val uiState: StateFlow<ClientServiceListUiState> = _uiState

    private var currentPage = 0

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getServicesUseCase(page = 0)
                .onSuccess { paginated ->
                    currentPage = 0
                    _uiState.update {
                        it.copy(
                            services = paginated.content,
                            isLoading = false,
                            hasMore = !paginated.last,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    if (_uiState.value.services.isEmpty()) {
                        _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar servicios") }
                    }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            getServicesUseCase(page = 0)
                .onSuccess { paginated ->
                    currentPage = 0
                    _uiState.update {
                        it.copy(
                            services = paginated.content,
                            isRefreshing = false,
                            hasMore = !paginated.last,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    if (_uiState.value.services.isEmpty()) {
                        _uiState.update { it.copy(isRefreshing = false, error = e.message ?: "Error al cargar servicios") }
                    }
                }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || !state.hasMore) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val nextPage = currentPage + 1
            getServicesUseCase(page = nextPage)
                .onSuccess { paginated ->
                    currentPage = nextPage
                    _uiState.update {
                        it.copy(
                            services = it.services + paginated.content,
                            isLoading = false,
                            hasMore = !paginated.last
                        )
                    }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
