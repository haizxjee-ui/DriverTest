package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.drivertest.data.local.entity.ExamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {

    @Insert
    suspend fun insertExam(exam: ExamEntity): Long

    @Query("SELECT * FROM exams ORDER BY startTime DESC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE examID = :examID")
    suspend fun getExamByID(examID: Int): ExamEntity?

    @Query(
        "SELECT * FROM exams " +
                "WHERE licenseTypeID = :licenseTypeID " +
                "ORDER BY startTime DESC"
    )
    fun getExamsByLicenseTypeID(licenseTypeID: Int): Flow<List<ExamEntity>>

    @Query("DELETE FROM exams")
    suspend fun deleteAllExams()
}