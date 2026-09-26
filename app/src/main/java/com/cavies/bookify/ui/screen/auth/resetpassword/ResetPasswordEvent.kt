package com.cavies.bookify.ui.screen.auth.resetpassword

sealed class ResetPasswordEvent {
    data object NavigateToLogin : ResetPasswordEvent()
}
