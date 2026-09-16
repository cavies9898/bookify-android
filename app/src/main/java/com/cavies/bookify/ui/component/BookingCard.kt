package com.cavies.bookify.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.cavies.bookify.core.domain.model.Booking
import com.cavies.bookify.core.domain.model.BookingStatus
import com.cavies.bookify.core.domain.util.DateUtils
import com.cavies.bookify.ui.theme.GreenBadge
import com.cavies.bookify.ui.theme.GreenBadgeBg
import com.cavies.bookify.ui.theme.RedBadge
import com.cavies.bookify.ui.theme.RedBadgeBg
import com.cavies.bookify.ui.theme.YellowBadge
import com.cavies.bookify.ui.theme.YellowBadgeBg

@Composable
fun BookingCard(
    booking: Booking,
    serviceName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = serviceName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            val badgeColor = when (booking.status) {
                BookingStatus.CONFIRMED -> GreenBadge
                BookingStatus.CANCELLED -> RedBadge
                BookingStatus.PENDING -> YellowBadge
            }
            val badgeBg = when (booking.status) {
                BookingStatus.CONFIRMED -> GreenBadgeBg
                BookingStatus.CANCELLED -> RedBadgeBg
                BookingStatus.PENDING -> YellowBadgeBg
            }
            val badgeText = when (booking.status) {
                BookingStatus.CONFIRMED -> "Confirmada"
                BookingStatus.CANCELLED -> "Cancelada"
                BookingStatus.PENDING -> "Pendiente"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = DateUtils.formatDisplayRange(booking.startAt, booking.endAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
