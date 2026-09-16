package com.cavies.bookify.core.network.dto

import com.cavies.bookify.core.domain.model.Pageable
import com.cavies.bookify.core.domain.model.PaginatedResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponseDto<T>(
    @SerialName("content") val content: List<T>,
    @SerialName("totalElements") val totalElements: Long,
    @SerialName("totalPages") val totalPages: Int,
    @SerialName("last") val last: Boolean,
    @SerialName("pageable") val pageable: PageableDto?
)

@Serializable
data class PageableDto(
    @SerialName("pageNumber") val pageNumber: Int,
    @SerialName("pageSize") val pageSize: Int
)

inline fun <T, R> PaginatedResponseDto<T>.toDomain(mapper: (T) -> R): PaginatedResponse<R> {
    return PaginatedResponse(
        content = content.map(mapper),
        totalElements = totalElements.toInt(),
        totalPages = totalPages,
        last = last,
        pageable = pageable?.let { Pageable(it.pageNumber, it.pageSize) }
    )
}
