package com.cavies.bookify.core.domain.repository

import com.cavies.bookify.core.domain.model.Booking
import com.cavies.bookify.core.domain.model.PaginatedResponse

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
