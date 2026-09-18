package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drivertest.data.local.entity.ImageEntity

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>): List<Long>

    @Query("SELECT * FROM images WHERE questionID = :questionID ORDER BY imageOrder")
    suspend fun getImagesByQuestionID(questionID: Int): List<ImageEntity>

    @Query("SELECT * FROM images WHERE imageID = :imageID")
    suspend fun getImageByID(imageID: Int): ImageEntity?

    @Query("DELETE FROM images")
    suspend fun deleteAllImages()
}