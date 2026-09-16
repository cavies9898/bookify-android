package com.cavies.bookify.core.domain.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    private val displayFormatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale("es"))
    private val queryFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun formatDisplay(isoString: String): String {
        return try {
            val instant = Instant.parse(isoString)
            val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            localDateTime.format(displayFormatter)
        } catch (_: Exception) {
            isoString
        }
    }

    fun formatDisplayRange(startAt: String, endAt: String): String {
        return "${formatDisplay(startAt)} - ${formatDisplay(endAt).substringAfter(", ")}"
    }

    fun toQueryString(localDate: java.time.LocalDate): String {
        return localDate.format(queryFormatter)
    }

    fun todayQueryString(): String {
        return java.time.LocalDate.now().format(queryFormatter)
    }
}
