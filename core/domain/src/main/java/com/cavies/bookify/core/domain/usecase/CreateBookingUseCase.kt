package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.model.Booking
import com.cavies.bookify.core.domain.repository.BookingRepository
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(
        serviceId: Long,
        startAt: String,
        endAt: String
    ): Result<Booking> =
        repository.createBooking(serviceId, startAt, endAt)
}
