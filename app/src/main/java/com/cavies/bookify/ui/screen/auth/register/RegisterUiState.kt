package com.cavies.bookify.ui.screen.auth.register

import com.cavies.bookify.core.domain.usecase.RegisterUseCase

data class RegisterUiState(
    val isLoading: Boolean = false,
    val serverError: String? = null,
    val nameError: RegisterUseCase.ValidationError? = null,
    val emailError: RegisterUseCase.ValidationError? = null,
    val passwordError: RegisterUseCase.ValidationError? = null
)
