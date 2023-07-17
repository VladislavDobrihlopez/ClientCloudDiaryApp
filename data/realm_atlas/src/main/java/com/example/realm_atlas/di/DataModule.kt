package com.example.realm_atlas.di

import android.content.Context
import androidx.room.Room
import com.example.realm_atlas.LocalDataSourceConstants
import com.example.realm_atlas.database.ImagesDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Provides
    @Singleton
    internal fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            ImagesDb::class.java,
            LocalDataSourceConstants.imagesForUploadingDbName
        ).build()

    @Singleton
    @Provides
    internal fun provideUploadingDao(db: ImagesDb) = db.imageInQueryForUploadingDaoService()

    @Singleton
    @Provides
    internal fun provideDeletionDao(db: ImagesDb) = db.imageInQueryForDeletionDaoService()
}