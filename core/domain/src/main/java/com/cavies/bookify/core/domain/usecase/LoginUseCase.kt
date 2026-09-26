package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.AuthResponse
import com.cavies.bookify.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    sealed class ValidationError {
        data object EmptyEmail : ValidationError()
        data object InvalidEmail : ValidationError()
        data object EmptyPassword : ValidationError()
        data object ShortPassword : ValidationError()
    }

    fun validate(email: String, password: String): ValidationError? {
        if (email.isBlank()) return ValidationError.EmptyEmail
        if (!email.contains("@") || !email.contains(".")) return ValidationError.InvalidEmail
        if (password.isBlank()) return ValidationError.EmptyPassword
        if (password.length < 6) return ValidationError.ShortPassword
        return null
    }

    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        val validationError = validate(email.trim(), password)
        if (validationError != null) {
            return Result.failure(ValidationException(validationError))
        }
        return repository.login(email.trim(), password)
    }
}

class ValidationException(val error: Any) :
    Exception("Validation failed")
