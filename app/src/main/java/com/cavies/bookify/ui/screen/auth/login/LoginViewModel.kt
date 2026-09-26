package com.cavies.bookify.ui.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.model.UserRole
import com.cavies.bookify.core.domain.usecase.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    emailError = null,
                    passwordError = null
                )
            }
            loginUseCase(email, password)
                .onSuccess { response ->
                    val role = response.user?.role ?: UserRole.CLIENTE
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(LoginEvent.NavigateTo(role))
                }
                .onFailure { e ->
                    when (e) {
                        is ValidationException -> handleValidationError(e.error as LoginUseCase.ValidationError)
                        else -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = e.message ?: "Error desconocido"
                            )
                        }
                    }
                }
        }
    }

    fun clearEmailError() {
        _uiState.update { it.copy(emailError = null) }
    }

    fun clearPasswordError() {
        _uiState.update { it.copy(passwordError = null) }
    }

    private fun handleValidationError(error: LoginUseCase.ValidationError) {
        _uiState.update { state ->
            when (error) {
                is LoginUseCase.ValidationError.EmptyEmail -> state.copy(
                    isLoading = false,
                    emailError = error
                )
                is LoginUseCase.ValidationError.InvalidEmail -> state.copy(
                    isLoading = false,
                    emailError = error
                )
                is LoginUseCase.ValidationError.EmptyPassword -> state.copy(
                    isLoading = false,
                    passwordError = error
                )
                is LoginUseCase.ValidationError.ShortPassword -> state.copy(
                    isLoading = false,
                    passwordError = error
                )
            }
        }
    }
}
