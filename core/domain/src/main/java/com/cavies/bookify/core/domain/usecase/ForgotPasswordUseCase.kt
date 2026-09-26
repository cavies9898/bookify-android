package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    sealed class ValidationError {
        data object EmptyEmail : ValidationError()
        data object InvalidEmail : ValidationError()
    }

    fun validate(email: String): ValidationError? {
        if (email.isBlank()) return ValidationError.EmptyEmail
        if (!email.contains("@") || !email.contains(".")) return ValidationError.InvalidEmail
        return null
    }

    suspend operator fun invoke(email: String): Result<String> {
        val validationError = validate(email.trim())
        if (validationError != null) {
            return Result.failure(ValidationException(validationError))
        }
        return repository.forgotPassword(email.trim())
    }
}
