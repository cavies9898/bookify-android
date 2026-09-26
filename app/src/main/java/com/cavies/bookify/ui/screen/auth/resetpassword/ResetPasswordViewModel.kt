package com.cavies.bookify.ui.screen.auth.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState

    private val _events = Channel<ResetPasswordEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun resetPassword(token: String, newPassword: String, confirmPassword: String) {
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
            resetPasswordUseCase(token, newPassword, confirmPassword)
                .onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = message,
                            resetSuccess = true
                        )
                    }
                }
                .onFailure { e ->
                    when (e) {
                        is ValidationException -> handleValidationError(e.error as ResetPasswordUseCase.ValidationError)
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

    private fun handleValidationError(error: ResetPasswordUseCase.ValidationError) {
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
