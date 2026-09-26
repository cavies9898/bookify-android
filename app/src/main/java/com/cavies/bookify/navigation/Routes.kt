package com.cavies.bookify.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password/{email}"
    const val CLIENT_TABS = "client_tabs"
    const val ADMIN_TABS = "admin_tabs"

    fun resetPassword(email: String) = "reset_password/$email"
}
