package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}
