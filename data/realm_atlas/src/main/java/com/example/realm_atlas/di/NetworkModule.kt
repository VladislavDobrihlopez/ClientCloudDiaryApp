package com.example.realm_atlas.di

import com.example.realm_atlas.MongoDbRepository
import com.example.realm_atlas.repositoryImpl.MongoDbRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Singleton
    @Provides
    internal fun provideRemoteDb(): MongoDbRepository {
        return MongoDbRepositoryImpl
    }
}