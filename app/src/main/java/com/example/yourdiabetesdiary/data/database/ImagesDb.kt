package com.example.yourdiabetesdiary.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.yourdiabetesdiary.data.LocalDataSource
import com.example.yourdiabetesdiary.data.database.models.ImagesForUploadingDbModel

@Database(entities = [ImagesForUploadingDbModel::class], version = 1, exportSchema = false)
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

    abstract fun imageInQueryForUploadingApiService(): ImageInQueryForUploadingDao
}