package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.PaginatedResponse
import com.cavies.bookify.core.domain.model.Service
import com.cavies.bookify.core.domain.repository.ServiceRepository
import javax.inject.Inject

class GetServicesUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(page: Int, size: Int = 10): Result<PaginatedResponse<Service>> =
        repository.getServices(page, size)
}
