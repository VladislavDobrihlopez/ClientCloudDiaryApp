package com.example.realm_atlas.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.realm_atlas.LocalDataSourceConstants

@Entity(tableName = LocalDataSourceConstants.imagesForDeletionEntityName)
data class ImagesForDeletionDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remotePath: String,
)