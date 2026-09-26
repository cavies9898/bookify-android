package com.cavies.bookify.ui.screen.auth.login

import com.cavies.bookify.core.domain.model.UserRole

sealed class LoginEvent {
    data class NavigateTo(val role: UserRole) : LoginEvent()
}
