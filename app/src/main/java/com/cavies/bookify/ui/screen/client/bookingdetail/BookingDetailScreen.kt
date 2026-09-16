package com.cavies.bookify.ui.screen.client.bookingdetail

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cavies.bookify.core.domain.model.BookingStatus
import com.cavies.bookify.core.domain.util.DateUtils
import com.cavies.bookify.ui.component.ActionCardButton
import com.cavies.bookify.ui.component.LocationMapCard
import com.cavies.bookify.ui.theme.GreenBadge
import com.cavies.bookify.ui.theme.RedBadge
import com.cavies.bookify.ui.theme.YellowBadge
import java.time.Instant
import java.time.ZoneId

private data class StatusInfo(
    val color: androidx.compose.ui.graphics.Color,
    val text: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BookingDetailScreen(
    bookingId: Long,
    navController: NavController,
    viewModel: BookingDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(bookingId) {
        viewModel.loadBooking(bookingId)
    }

    uiState.booking?.let { b ->
        val statusInfo = when (b.status) {
            BookingStatus.CONFIRMED -> StatusInfo(GreenBadge, "Confirmada", Icons.Default.CheckCircle)
            BookingStatus.CANCELLED -> StatusInfo(RedBadge, "Cancelada", Icons.Default.Block)
            BookingStatus.PENDING -> StatusInfo(YellowBadge, "Pendiente", Icons.Default.HourglassEmpty)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
                Text(
                    text = uiState.serviceName,
                    style = MaterialTheme.typography.titleMedium
                )
                if (uiState.serviceDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.serviceDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        TimeRow(label = "Inicio", time = DateUtils.formatDisplay(b.startAt))
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))
                        TimeRow(label = "Fin", time = DateUtils.formatDisplay(b.endAt))
                        Spacer(modifier = Modifier.height(12.dp))
                        StatusBadge(statusInfo)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ActionCardButton(
                    icon = Icons.Default.CalendarMonth,
                    label = "Agregar al calendario",
                    onClick = {
                        val startMillis = parseToMillis(b.startAt) ?: System.currentTimeMillis()
                        val endMillis = parseToMillis(b.endAt) ?: (startMillis + 3600000)

                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, uiState.serviceName)
                            putExtra(CalendarContract.Events.DESCRIPTION, uiState.serviceDescription)
                            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                        }
                        context.startActivity(intent)
                    }
                )

                if (b.status == BookingStatus.CONFIRMED) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ActionCardButton(
                        icon = Icons.Default.Block,
                        label = "Cancelar Reserva",
                        onClick = { viewModel.cancelBooking(b.id) { viewModel.loadBooking(b.id) } }
                    )
                }

                val serviceLocation = uiState.serviceLocation
                if (!serviceLocation.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LocationMapCard(
                        location = serviceLocation,
                        latitude = uiState.serviceLatitude,
                        longitude = uiState.serviceLongitude
                    )
                }
            }
        }
}

@Composable
private fun TimeRow(label: String, time: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = time, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatusBadge(info: StatusInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(info.color.copy(alpha = 0.15f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = info.icon, contentDescription = null, tint = info.color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Estado: ${info.text}", color = info.color, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

private fun parseToMillis(isoString: String): Long? {
    return try {
        Instant.parse(isoString).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } catch (_: Exception) {
        null
    }
}
