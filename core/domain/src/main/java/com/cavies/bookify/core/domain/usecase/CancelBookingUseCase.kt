package com.cavies.bookify.core.domain.usecase

import com.cavies.bookify.core.domain.repository.BookingRepository
import javax.inject.Inject

class CancelBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        repository.cancelBooking(id)
}
