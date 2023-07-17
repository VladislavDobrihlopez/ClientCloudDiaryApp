package com.example.yourdiabetesdiary.di.modules

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
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            com.example.realm_atlas.database.ImagesDb::class.java,
            com.example.realm_atlas.LocalDataSourceConstants.imagesForUploadingDbName
        ).build()

    @Singleton
    @Provides
    fun provideUploadingDao(db: com.example.realm_atlas.database.ImagesDb) = db.imageInQueryForUploadingDaoService()

    @Singleton
    @Provides
    fun provideDeletionDao(db: com.example.realm_atlas.database.ImagesDb) = db.imageInQueryForDeletionDaoService()
}