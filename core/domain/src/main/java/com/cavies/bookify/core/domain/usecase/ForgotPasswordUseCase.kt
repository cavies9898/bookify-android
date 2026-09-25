package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<String> =
        repository.forgotPassword(email)
}
