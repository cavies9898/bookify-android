package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String, newPassword: String): Result<String> =
        repository.resetPassword(token, newPassword)
}
