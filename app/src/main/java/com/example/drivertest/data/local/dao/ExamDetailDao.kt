package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drivertest.data.local.entity.ExamDetailEntity

@Dao
interface ExamDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamDetails(examDetails: List<ExamDetailEntity>): List<Long>

    @Query(
        "SELECT * FROM exam_details " +
                "WHERE examID = :examID " +
                "ORDER BY questionOrder"
    )
    suspend fun getExamDetailsByExamID(examID: Int): List<ExamDetailEntity>

    @Query("SELECT * FROM exam_details WHERE examDetailID = :examDetailID")
    suspend fun getExamDetailByID(examDetailID: Int): ExamDetailEntity?

    @Query(
        "SELECT * FROM exam_details " +
                "WHERE examID = :examID " +
                "AND isCorrect = 0"
    )
    suspend fun getWrongAnswersByExamID(examID: Int): List<ExamDetailEntity>

    @Query("DELETE FROM exam_details WHERE examID = :examID")
    suspend fun deleteExamDetailsByExamID(examID: Int)

    @Query("DELETE FROM exam_details")
    suspend fun deleteAllExamDetails()
}