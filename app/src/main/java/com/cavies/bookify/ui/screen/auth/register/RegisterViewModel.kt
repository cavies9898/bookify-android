package com.cavies.bookify.ui.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.usecase.RegisterUseCase
import com.cavies.bookify.core.domain.usecase.ValidationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _events = Channel<RegisterEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    serverError = null,
                    nameError = null,
                    emailError = null,
                    passwordError = null
                )
            }
            registerUseCase(name, email, password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(RegisterEvent.NavigateToClient)
                }
                .onFailure { e ->
                    when (e) {
                        is ValidationException -> handleValidationError(e.error as RegisterUseCase.ValidationError)
                        else -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                serverError = e.message ?: "Error del servidor"
                            )
                        }
                    }
                }
        }
    }

    fun clearNameError() {
        _uiState.update { it.copy(nameError = null) }
    }

    fun clearEmailError() {
        _uiState.update { it.copy(emailError = null) }
    }

    fun clearPasswordError() {
        _uiState.update { it.copy(passwordError = null) }
    }

    private fun handleValidationError(error: RegisterUseCase.ValidationError) {
        _uiState.update { state ->
            when (error) {
                is RegisterUseCase.ValidationError.EmptyName -> state.copy(
                    isLoading = false,
                    nameError = error
                )
                is RegisterUseCase.ValidationError.ShortName -> state.copy(
                    isLoading = false,
                    nameError = error
                )
                is RegisterUseCase.ValidationError.EmptyEmail -> state.copy(
                    isLoading = false,
                    emailError = error
                )
                is RegisterUseCase.ValidationError.InvalidEmail -> state.copy(
                    isLoading = false,
                    emailError = error
                )
                is RegisterUseCase.ValidationError.EmptyPassword -> state.copy(
                    isLoading = false,
                    passwordError = error
                )
                is RegisterUseCase.ValidationError.ShortPassword -> state.copy(
                    isLoading = false,
                    passwordError = error
                )
            }
        }
    }
}
