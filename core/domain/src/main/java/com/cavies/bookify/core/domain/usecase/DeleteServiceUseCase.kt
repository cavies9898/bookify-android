package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.repository.ServiceRepository
import javax.inject.Inject

class DeleteServiceUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        repository.deleteService(id)
}
