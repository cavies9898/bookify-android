package com.cavies.bookify.core.data.repository

import com.cavies.bookify.core.domain.model.PaginatedResponse
import com.cavies.bookify.core.domain.model.Service
import com.cavies.bookify.core.domain.model.AvailabilitySlot
import com.cavies.bookify.core.network.api.ApiService
import com.cavies.bookify.core.network.dto.CreateServiceRequest
import com.cavies.bookify.core.network.dto.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ServiceRepository {

    override suspend fun getServices(page: Int, size: Int): Result<PaginatedResponse<Service>> {
        return try {
            val response = api.getServices(page, size)
            Result.success(response.toDomain { it.toDomain() })
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception(mapError(e.code())))
        } catch (e: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    override suspend fun getServiceAvailability(
        serviceId: Long,
        date: String
    ): Result<List<AvailabilitySlot>> {
        return try {
            val response = api.getAvailability(serviceId, date)
            Result.success(response.slots.map { it.toDomain() })
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception(mapError(e.code())))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    override suspend fun createService(request: CreateServiceRequest): Result<Service> {
        return try {
            val response = api.createService(request)
            Result.success(response.toDomain())
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception(mapError(e.code())))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    override suspend fun updateService(id: Long, request: CreateServiceRequest): Result<Service> {
        return try {
            val response = api.updateService(id, request)
            Result.success(response.toDomain())
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception(mapError(e.code())))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    override suspend fun deleteService(id: Long): Result<Unit> {
        return try {
            api.deleteService(id)
            Result.success(Unit)
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception(mapError(e.code())))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    private fun mapError(code: Int): String = when (code) {
        401 -> "Credenciales incorrectas"
        403 -> "No tienes permiso"
        404 -> "Recurso no encontrado"
        422 -> "Datos inválidos"
        else -> "Error del servidor"
    }
}
