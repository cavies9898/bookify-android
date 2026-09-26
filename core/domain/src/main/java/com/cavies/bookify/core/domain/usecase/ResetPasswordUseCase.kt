package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    sealed class ValidationError {
        data object EmptyCode : ValidationError()
        data object InvalidCodeLength : ValidationError()
        data object EmptyPassword : ValidationError()
        data object ShortPassword : ValidationError()
        data object PasswordsDoNotMatch : ValidationError()
    }

    fun validate(code: String, newPassword: String, confirmPassword: String): ValidationError? {
        if (code.isBlank()) return ValidationError.EmptyCode
        if (code.length != 6) return ValidationError.InvalidCodeLength
        if (newPassword.isBlank()) return ValidationError.EmptyPassword
        if (newPassword.length < 8) return ValidationError.ShortPassword
        if (newPassword != confirmPassword) return ValidationError.PasswordsDoNotMatch
        return null
    }

    suspend operator fun invoke(
        token: String,
        newPassword: String,
        confirmPassword: String
    ): Result<String> {
        val validationError = validate(token, newPassword, confirmPassword)
        if (validationError != null) {
            return Result.failure(ValidationException(validationError))
        }
        return repository.resetPassword(token, newPassword)
    }
}
