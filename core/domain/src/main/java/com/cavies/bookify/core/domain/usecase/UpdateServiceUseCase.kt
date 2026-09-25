package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.Service
import com.cavies.bookify.core.domain.repository.CreateServiceParams
import com.cavies.bookify.core.domain.repository.ServiceRepository
import javax.inject.Inject

class UpdateServiceUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(id: Long, params: CreateServiceParams): Result<Service> =
        repository.updateService(id, params)
}
