package com.cavies.bookify.core.data.di

import com.cavies.bookify.core.data.local.TokenManager
import com.cavies.bookify.core.data.repository.AuthRepository
import com.cavies.bookify.core.data.repository.AuthRepositoryImpl
import com.cavies.bookify.core.data.repository.BookingRepository
import com.cavies.bookify.core.data.repository.BookingRepositoryImpl
import com.cavies.bookify.core.data.repository.ServiceRepository
import com.cavies.bookify.core.data.repository.ServiceRepositoryImpl
import com.cavies.bookify.core.network.interceptor.TokenProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(impl: ServiceRepositoryImpl): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(impl: BookingRepositoryImpl): BookingRepository
}

@Module
@InstallIn(SingletonComponent::class)
object TokenProviderModule {

    @Provides
    @Singleton
    fun provideTokenProvider(tokenManager: TokenManager): TokenProvider {
        return object : TokenProvider {
            override suspend fun getAccessToken() = tokenManager.getAccessToken()
            override suspend fun getRefreshToken() = tokenManager.getRefreshToken()
            override suspend fun saveTokens(accessToken: String, refreshToken: String) =
                tokenManager.saveTokens(accessToken, refreshToken)

            override suspend fun clearTokens() = tokenManager.clearAll()
        }
    }
}
