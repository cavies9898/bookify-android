package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.AvailabilitySlot
import com.cavies.bookify.core.domain.repository.ServiceRepository
import javax.inject.Inject

class GetServiceAvailabilityUseCase @Inject constructor(
    private val repository: ServiceRepository
) {
    suspend operator fun invoke(serviceId: Long, date: String): Result<List<AvailabilitySlot>> =
        repository.getServiceAvailability(serviceId, date)
}
