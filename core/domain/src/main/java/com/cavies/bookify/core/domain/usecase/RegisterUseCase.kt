package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.AuthResponse
import com.cavies.bookify.core.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<AuthResponse> =
        repository.register(name, email, password)
}
