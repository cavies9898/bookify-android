package com.cavies.bookify.ui.screen.auth.passwordrecovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cavies.bookify.core.domain.usecase.ForgotPasswordUseCase
import com.cavies.bookify.core.domain.usecase.ResetPasswordUseCase
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
class PasswordRecoveryViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordRecoveryUiState())
    val uiState: StateFlow<PasswordRecoveryUiState> = _uiState

    private val _events = Channel<PasswordRecoveryEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    serverError = null,
                    emailError = null
                )
            }
            forgotPasswordUseCase(email)
                .onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = message,
                            email = email,
                            step = 2
                        )
                    }
                }
                .onFailure { e ->
                    when (e) {
                        is ValidationException -> handleEmailValidationError(e.error as ForgotPasswordUseCase.ValidationError)
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

    fun resetPassword(code: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    serverError = null,
                    codeError = null,
                    passwordError = null,
                    confirmPasswordError = null
                )
            }
            resetPasswordUseCase(code, newPassword, confirmPassword)
                .onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = message,
                            step = 3
                        )
                    }
                }
                .onFailure { e ->
                    when (e) {
                        is ValidationException -> handleResetValidationError(e.error as ResetPasswordUseCase.ValidationError)
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

    fun clearEmailError() {
        _uiState.update { it.copy(emailError = null) }
    }

    fun clearCodeError() {
        _uiState.update { it.copy(codeError = null) }
    }

    fun clearPasswordErrors() {
        _uiState.update {
            it.copy(
                passwordError = null,
                confirmPasswordError = null
            )
        }
    }

    private fun handleEmailValidationError(error: ForgotPasswordUseCase.ValidationError) {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                emailError = error
            )
        }
    }

    private fun handleResetValidationError(error: ResetPasswordUseCase.ValidationError) {
        _uiState.update { state ->
            when (error) {
                is ResetPasswordUseCase.ValidationError.EmptyCode -> state.copy(
                    isLoading = false,
                    codeError = error
                )
                is ResetPasswordUseCase.ValidationError.InvalidCodeLength -> state.copy(
                    isLoading = false,
                    codeError = error
                )
                is ResetPasswordUseCase.ValidationError.EmptyPassword -> state.copy(
                    isLoading = false,
                    passwordError = error
                )
                is ResetPasswordUseCase.ValidationError.ShortPassword -> state.copy(
                    isLoading = false,
                    passwordError = error
                )
                is ResetPasswordUseCase.ValidationError.PasswordsDoNotMatch -> state.copy(
                    isLoading = false,
                    confirmPasswordError = error
                )
            }
        }
    }
}
