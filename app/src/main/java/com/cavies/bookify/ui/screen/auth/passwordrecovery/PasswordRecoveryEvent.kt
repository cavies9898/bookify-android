package com.cavies.bookify.ui.screen.auth.passwordrecovery

sealed class PasswordRecoveryEvent {
    data object NavigateToLogin : PasswordRecoveryEvent()
}
