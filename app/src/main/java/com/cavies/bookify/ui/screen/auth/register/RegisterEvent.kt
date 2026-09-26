package com.cavies.bookify.ui.screen.auth.register

sealed class RegisterEvent {
    data object NavigateToClient : RegisterEvent()
}
