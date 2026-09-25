package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.Service
import com.cavies.bookify.core.domain.repository.ServiceRepository
import javax.inject.Inject

class GetServiceByIdUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(serviceId: Long): Result<Service?> {
        return repository.getServices(page = 0, size = 100)
            .map { paginated -> paginated.content.find { it.id == serviceId } }
    }
}
