package com.cavies.bookify.core.data.repository

import com.cavies.bookify.core.domain.model.AuthResponse
import com.cavies.bookify.core.domain.model.AvailabilitySlot
import com.cavies.bookify.core.domain.model.Booking
import com.cavies.bookify.core.domain.model.PaginatedResponse
import com.cavies.bookify.core.domain.model.Service
import com.cavies.bookify.core.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResponse>
    suspend fun register(name: String, email: String, password: String): Result<AuthResponse>
    suspend fun forgotPassword(email: String): Result<String>
    suspend fun resetPassword(token: String, newPassword: String): Result<String>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): User?
}

interface ServiceRepository {
    suspend fun getServices(page: Int, size: Int = 10): Result<PaginatedResponse<Service>>
    suspend fun getServiceAvailability(serviceId: Long, date: String): Result<List<AvailabilitySlot>>
    suspend fun createService(request: com.cavies.bookify.core.network.dto.CreateServiceRequest): Result<Service>
    suspend fun updateService(id: Long, request: com.cavies.bookify.core.network.dto.CreateServiceRequest): Result<Service>
    suspend fun deleteService(id: Long): Result<Unit>
}

interface BookingRepository {
    suspend fun getBookings(
        page: Int,
        size: Int = 10,
        status: String? = null
    ): Result<PaginatedResponse<Booking>>

    suspend fun createBooking(
        serviceId: Long,
        startAt: String,
        endAt: String
    ): Result<Booking>

    suspend fun cancelBooking(id: Long): Result<Unit>
}
