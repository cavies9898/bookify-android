package com.cavies.bookify.ui.screen.auth.passwordrecovery

data class PasswordRecoveryUiState(
    val step: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val email: String = ""
)
