package com.example.yourdiabetesdiary.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yourdiabetesdiary.data.LocalDataSource

@Entity(tableName = LocalDataSource.imagesForUploadingEntityName)
data class ImagesForUploadingDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val localUri: String,
    val remotePath: String,
    val sessionUri: String
)