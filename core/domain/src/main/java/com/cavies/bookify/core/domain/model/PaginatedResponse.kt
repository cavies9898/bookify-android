package com.cavies.bookify.core.domain.model

data class PaginatedResponse<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean,
    val pageable: Pageable?
)

data class Pageable(
    val pageNumber: Int,
    val pageSize: Int
)
