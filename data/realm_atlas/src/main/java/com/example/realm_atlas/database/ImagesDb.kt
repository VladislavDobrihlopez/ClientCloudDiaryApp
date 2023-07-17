package com.example.realm_atlas.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.realm_atlas.database.dao.ImageInQueryForDeletionDao
import com.example.realm_atlas.database.dao.ImageInQueryForUploadingDao
import com.example.realm_atlas.database.models.ImagesForDeletionDbModel
import com.example.realm_atlas.database.models.ImagesForUploadingDbModel

@Database(
    entities = [ImagesForUploadingDbModel::class, ImagesForDeletionDbModel::class],
    version = 3,
    exportSchema = false
)
abstract class ImagesDb : RoomDatabase() {
//    private var _instance: ImagesDb? = null
//    private val monitor = Object()
//    fun getInstance(context: Context) = run {
//        synchronized(monitor) {
//            if (_instance == null) {
//                _instance = Room.databaseBuilder(
//                    context,
//                    ImagesDb::class.java,
//                    LocalDataSource.imagesForUploadingDbName
//                ).build()
//            }
//            _instance!!
//        }
//    }

    abstract fun imageInQueryForUploadingDaoService(): ImageInQueryForUploadingDao
    abstract fun imageInQueryForDeletionDaoService(): ImageInQueryForDeletionDao
}