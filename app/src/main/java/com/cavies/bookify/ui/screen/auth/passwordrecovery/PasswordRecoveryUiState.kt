package com.cavies.bookify.ui.screen.auth.passwordrecovery

import com.cavies.bookify.core.domain.usecase.ForgotPasswordUseCase
import com.cavies.bookify.core.domain.usecase.ResetPasswordUseCase

data class PasswordRecoveryUiState(
    val step: Int = 1,
    val isLoading: Boolean = false,
    val serverError: String? = null,
    val emailError: ForgotPasswordUseCase.ValidationError? = null,
    val codeError: ResetPasswordUseCase.ValidationError? = null,
    val passwordError: ResetPasswordUseCase.ValidationError? = null,
    val confirmPasswordError: ResetPasswordUseCase.ValidationError? = null,
    val successMessage: String? = null,
    val email: String = ""
)
