package com.example.yourdiabetesdiary.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yourdiabetesdiary.data.LocalDataSource

@Entity(tableName = LocalDataSource.imagesForDeletionEntityName)
data class ImagesForDeletionDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remotePath: String,
)