package com.example.yourdiabetesdiary.di.modules

import android.content.Context
import com.example.util.connectivity.NetworkConnectivityObserver
import com.example.yourdiabetesdiary.data.repositoryImpl.MongoDbRepositoryImpl
import com.example.yourdiabetesdiary.domain.MongoDbRepository
import com.example.util.connectivity.ConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideConnectivityImpl(@ApplicationContext context: Context): ConnectivityObserver =
        NetworkConnectivityObserver(context = context)

    @Singleton
    @Provides
    fun provideRemoteDb(): MongoDbRepository {
        return MongoDbRepositoryImpl
    }
}