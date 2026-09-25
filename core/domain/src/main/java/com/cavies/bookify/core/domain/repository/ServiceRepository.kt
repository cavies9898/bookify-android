package com.cavies.bookify.core.domain.repository

import com.cavies.bookify.core.domain.model.AvailabilitySlot
import com.cavies.bookify.core.domain.model.PaginatedResponse
import com.cavies.bookify.core.domain.model.Service

interface ServiceRepository {
    suspend fun getServices(page: Int, size: Int = 10): Result<PaginatedResponse<Service>>
    suspend fun getServiceAvailability(serviceId: Long, date: String): Result<List<AvailabilitySlot>>
    suspend fun createService(request: CreateServiceParams): Result<Service>
    suspend fun updateService(id: Long, request: CreateServiceParams): Result<Service>
    suspend fun deleteService(id: Long): Result<Unit>
}

data class CreateServiceParams(
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val capacity: Int,
    val price: Double,
    val openingTime: String?,
    val closingTime: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?
)
