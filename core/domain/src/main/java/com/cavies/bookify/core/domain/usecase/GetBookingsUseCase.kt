package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.Booking
import com.cavies.bookify.core.domain.model.PaginatedResponse
import com.cavies.bookify.core.domain.repository.BookingRepository
import javax.inject.Inject

class GetBookingsUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int = 10,
        status: String? = null
    ): Result<PaginatedResponse<Booking>> =
        repository.getBookings(page, size, status)
}
