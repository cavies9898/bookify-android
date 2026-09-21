package com.cavies.bookify.di

import com.cavies.bookify.core.network.di.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FlavorModule {

    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = "http://192.168.101.115:8080"
}
