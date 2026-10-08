package com.example.drivertest.data.local.dao

import PracticeProgressEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PracticeProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(
        progress: PracticeProgressEntity
    )

    @Query("""
    SELECT *
    FROM practice_progress
    WHERE chapterNumber = :chapterNumber
""")
    suspend fun getProgressByChapterNumber(
        chapterNumber: Int
    ): PracticeProgressEntity?

    @Query("DELETE FROM practice_progress")
    suspend fun deleteAllProgress()

    @Query("""
    DELETE FROM practice_progress
    WHERE chapterNumber = :chapterNumber
""")
    suspend fun deleteProgressByChapter(
        chapterNumber: Int
    )
}