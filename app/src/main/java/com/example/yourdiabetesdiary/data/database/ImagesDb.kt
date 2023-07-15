package com.example.yourdiabetesdiary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yourdiabetesdiary.data.database.dao.ImageInQueryForDeletionDao
import com.example.yourdiabetesdiary.data.database.dao.ImageInQueryForUploadingDao
import com.example.yourdiabetesdiary.data.database.models.ImagesForDeletionDbModel
import com.example.yourdiabetesdiary.data.database.models.ImagesForUploadingDbModel

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