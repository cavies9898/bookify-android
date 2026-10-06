package com.cavies.bookify.ui.component

import androidx.annotation.StringRes
import com.cavies.bookify.R

enum class BookingFilter(
    val status: String?,
    @StringRes val adminLabelRes: Int,
    @StringRes val clientLabelRes: Int
) {
    ALL(null, R.string.admin_bookings_filter_all, R.string.admin_bookings_filter_all),
    PENDING("PENDING", R.string.admin_bookings_filter_pending, R.string.admin_bookings_filter_pending),
    CONFIRMED("CONFIRMED", R.string.admin_bookings_filter_confirmed, R.string.admin_bookings_filter_confirmed),
    CANCELLED("CANCELLED", R.string.admin_bookings_filter_cancelled, R.string.admin_bookings_filter_cancelled)
}
