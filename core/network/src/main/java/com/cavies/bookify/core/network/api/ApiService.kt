package com.cavies.bookify.core.network.api

import com.cavies.bookify.core.network.dto.AuthResponseDto
import com.cavies.bookify.core.network.dto.AvailabilityResponseDto
import com.cavies.bookify.core.network.dto.AvailabilitySlotDto
import com.cavies.bookify.core.network.dto.BookingDto
import com.cavies.bookify.core.network.dto.CreateBookingRequest
import com.cavies.bookify.core.network.dto.CreateServiceRequest
import com.cavies.bookify.core.network.dto.ForgotPasswordRequest
import com.cavies.bookify.core.network.dto.LoginRequest
import com.cavies.bookify.core.network.dto.MessageResponse
import com.cavies.bookify.core.network.dto.PaginatedResponseDto
import com.cavies.bookify.core.network.dto.RefreshTokenRequest
import com.cavies.bookify.core.network.dto.RegisterRequest
import com.cavies.bookify.core.network.dto.ResetPasswordRequest
import com.cavies.bookify.core.network.dto.ServiceDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponseDto

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponseDto

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): MessageResponse

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): MessageResponse

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): AuthResponseDto

    @GET("api/services")
    suspend fun getServices(
        @Query("page") page: Int,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "name,asc"
    ): PaginatedResponseDto<ServiceDto>

    @GET("api/services/{id}/availability")
    suspend fun getAvailability(
        @Path("id") id: Long,
        @Query("date") date: String
    ): AvailabilityResponseDto

    @POST("api/services")
    suspend fun createService(@Body request: CreateServiceRequest): ServiceDto

    @PUT("api/services/{id}")
    suspend fun updateService(
        @Path("id") id: Long,
        @Body request: CreateServiceRequest
    ): ServiceDto

    @DELETE("api/services/{id}")
    suspend fun deleteService(@Path("id") id: Long)

    @GET("api/bookings")
    suspend fun getBookings(
        @Query("page") page: Int,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "startAt,desc",
        @Query("status") status: String? = null
    ): PaginatedResponseDto<BookingDto>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): BookingDto

    @DELETE("api/bookings/{id}")
    suspend fun cancelBooking(@Path("id") id: Long)
}
