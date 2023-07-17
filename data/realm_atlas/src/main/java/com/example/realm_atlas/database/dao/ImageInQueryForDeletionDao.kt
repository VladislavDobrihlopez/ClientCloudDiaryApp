package com.example.realm_atlas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.realm_atlas.database.models.ImagesForDeletionDbModel

@Dao
interface ImageInQueryForDeletionDao {
    @Query("SELECT * FROM images_for_deletion ORDER BY id ASC")
    suspend fun getAllImages(): List<ImagesForDeletionDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(model: ImagesForDeletionDbModel)

    @Query("DELETE FROM images_for_deletion WHERE id = :imageId")
    suspend fun clearAll(imageId: Int)
}