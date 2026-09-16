package com.cavies.bookify.core.data.repository

import com.cavies.bookify.core.domain.model.Booking
import com.cavies.bookify.core.domain.model.PaginatedResponse
import com.cavies.bookify.core.network.api.ApiService
import com.cavies.bookify.core.network.dto.CreateBookingRequest
import com.cavies.bookify.core.network.dto.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val api: ApiService
) : BookingRepository {

    override suspend fun getBookings(
        page: Int,
        size: Int,
        status: String?
    ): Result<PaginatedResponse<Booking>> {
        return try {
            val response = api.getBookings(page, size, status = status)
            Result.success(response.toDomain { it.toDomain() })
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception(mapError(e.code())))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    override suspend fun createBooking(
        serviceId: Long,
        startAt: String,
        endAt: String
    ): Result<Booking> {
        return try {
            val response = api.createBooking(CreateBookingRequest(serviceId, startAt, endAt))
            Result.success(response.toDomain())
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception(mapError(e.code())))
        } catch (_: Exception) {
            Result.failure(Exception("Error del servidor"))
        }
    }

    override suspend fun cancelBooking(id: Long): Result<Unit> {
        return try {
            api.cancelBooking(id)
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
