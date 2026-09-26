package com.cavies.bookify.ui.screen.auth.resetpassword

import com.cavies.bookify.core.domain.usecase.ResetPasswordUseCase

data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val serverError: String? = null,
    val codeError: ResetPasswordUseCase.ValidationError? = null,
    val passwordError: ResetPasswordUseCase.ValidationError? = null,
    val confirmPasswordError: ResetPasswordUseCase.ValidationError? = null,
    val resetSuccess: Boolean = false,
    val successMessage: String? = null
)
