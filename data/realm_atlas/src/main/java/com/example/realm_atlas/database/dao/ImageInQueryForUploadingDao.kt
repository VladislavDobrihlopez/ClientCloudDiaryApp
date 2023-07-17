package com.example.realm_atlas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.realm_atlas.database.models.ImagesForUploadingDbModel

@Dao
interface ImageInQueryForUploadingDao {
    @Query("SELECT * FROM images_for_uploading ORDER BY id ASC")
    suspend fun getAllImages(): List<ImagesForUploadingDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(model: ImagesForUploadingDbModel)

    @Query("DELETE FROM images_for_uploading WHERE id = :imageId")
    suspend fun clearAll(imageId: Int)
}