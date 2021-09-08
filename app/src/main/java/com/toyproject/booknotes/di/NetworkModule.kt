package com.toyproject.booknotes.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideUnauthorizedOkHttpClient(loggingInterceptor:HttpLoggingInterceptor):OkHttpClient
            = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideLoggingInterceptor():HttpLoggingInterceptor
            = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
}