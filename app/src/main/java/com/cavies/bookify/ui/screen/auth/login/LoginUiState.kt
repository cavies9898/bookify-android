package com.cavies.bookify.ui.screen.auth.login

import com.cavies.bookify.core.domain.model.UserRole
import com.cavies.bookify.core.domain.usecase.LoginUseCase

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: LoginUseCase.ValidationError? = null,
    val passwordError: LoginUseCase.ValidationError? = null
)

sealed class LoginEvent {
    data class NavigateTo(val role: UserRole) : LoginEvent()
}
