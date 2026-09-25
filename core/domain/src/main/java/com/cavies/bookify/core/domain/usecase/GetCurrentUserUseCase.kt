package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.User
import com.cavies.bookify.core.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): User? = repository.getCurrentUser()
}
