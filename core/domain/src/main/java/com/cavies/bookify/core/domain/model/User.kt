package com.cavies.bookify.core.domain.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val role: UserRole
)

enum class UserRole { ADMIN, CLIENTE }
