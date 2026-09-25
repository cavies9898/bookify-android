package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.Service
import com.cavies.bookify.core.domain.repository.ServiceRepository
import javax.inject.Inject

class GetServiceNamesUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(): Result<Map<Long, String>> =
        repository.getServices(page = 0, size = 100)
            .map { paginated -> paginated.content.associate { it.id to it.name } }
}
