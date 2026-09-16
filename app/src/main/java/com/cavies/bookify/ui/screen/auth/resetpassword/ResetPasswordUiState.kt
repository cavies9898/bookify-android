package com.cavies.bookify.ui.screen.auth.resetpassword

data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val resetSuccess: Boolean = false,
    val successMessage: String? = null
)
