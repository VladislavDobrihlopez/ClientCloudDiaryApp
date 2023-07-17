package com.example.util.di

import com.example.util.connectivity.ConnectivityObserver
import com.example.util.connectivity.NetworkConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UtilitiesModule {
    @Singleton
    @Binds
    internal abstract fun bindConnectivity(impl: NetworkConnectivityObserver): ConnectivityObserver
}