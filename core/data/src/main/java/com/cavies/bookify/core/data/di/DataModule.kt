package com.cavies.bookify.core.data.di

import com.cavies.bookify.core.data.local.TokenManager
import com.cavies.bookify.core.data.repository.AuthRepositoryImpl
import com.cavies.bookify.core.data.repository.BookingRepositoryImpl
import com.cavies.bookify.core.data.repository.ServiceRepositoryImpl
import com.cavies.bookify.core.domain.repository.AuthRepository
import com.cavies.bookify.core.domain.repository.BookingRepository
import com.cavies.bookify.core.domain.repository.ServiceRepository
import com.cavies.bookify.core.domain.repository.TokenProvider
import com.cavies.bookify.core.domain.repository.TokenStorage
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

    @Binds
    @Singleton
    abstract fun bindTokenStorage(impl: TokenManager): TokenStorage
}

@Module
@InstallIn(SingletonComponent::class)
object TokenProviderModule {

    @Provides
    @Singleton
    fun provideTokenProvider(tokenStorage: TokenStorage): TokenProvider {
        return object : TokenProvider {
            override suspend fun getAccessToken() = tokenStorage.getAccessToken()
            override suspend fun getRefreshToken() = tokenStorage.getRefreshToken()
            override suspend fun saveTokens(accessToken: String, refreshToken: String) =
                tokenStorage.saveTokens(accessToken, refreshToken)

            override suspend fun clearTokens() = tokenStorage.clearAll()
        }
    }
}
