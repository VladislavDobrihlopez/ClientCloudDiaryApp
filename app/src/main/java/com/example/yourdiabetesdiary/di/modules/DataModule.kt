package com.example.yourdiabetesdiary.di.modules

import android.content.Context
import androidx.room.Room
import com.example.yourdiabetesdiary.data.LocalDataSourceConstants
import com.example.yourdiabetesdiary.data.database.ImagesDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            ImagesDb::class.java,
            LocalDataSourceConstants.imagesForUploadingDbName
        ).build()

    @Singleton
    @Provides
    fun provideUploadingDao(db: ImagesDb) = db.imageInQueryForUploadingDaoService()

    @Singleton
    @Provides
    fun provideDeletionDao(db: ImagesDb) = db.imageInQueryForDeletionDaoService()
}