package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drivertest.data.local.entity.MockExamEntity
import com.example.drivertest.data.local.entity.MockExamQuestionEntity

@Dao
interface MockExamDao {

    // Lưu danh sách đề thi
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockExams(
        exams: List<MockExamEntity>
    ): List<Long>

    // Lưu câu hỏi của các đề
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockExamQuestions(
        questions: List<MockExamQuestionEntity>
    )

    // Lấy danh sách đề theo hạng bằng
    @Query("""
        SELECT * FROM mock_exams
        WHERE licenseTypeID = :licenseTypeID
        ORDER BY examNumber ASC
    """)
    suspend fun getMockExamsByLicenseType(
        licenseTypeID: Int
    ): List<MockExamEntity>

    // Lấy một đề theo ID
    @Query("""
        SELECT * FROM mock_exams
        WHERE mockExamID = :mockExamID
    """)
    suspend fun getMockExamByID(
        mockExamID: Int
    ): MockExamEntity?

    // Lấy các câu hỏi của một đề theo thứ tự
    @Query("""
        SELECT * FROM mock_exam_questions
        WHERE mockExamID = :mockExamID
        ORDER BY questionOrder ASC
    """)
    suspend fun getQuestionsByMockExamID(
        mockExamID: Int
    ): List<MockExamQuestionEntity>

    // Xóa toàn bộ câu hỏi thuộc một đề
    @Query("""
        DELETE FROM mock_exam_questions
        WHERE mockExamID = :mockExamID
    """)
    suspend fun deleteQuestionsByMockExamID(
        mockExamID: Int
    )

    // Xóa toàn bộ đề thuộc một hạng bằng
    @Query("""
        DELETE FROM mock_exams
        WHERE licenseTypeID = :licenseTypeID
    """)
    suspend fun deleteMockExamsByLicenseType(
        licenseTypeID: Int
    )

    // Đếm số đề đã tạo theo hạng bằng
    @Query("""
        SELECT COUNT(*) FROM mock_exams
        WHERE licenseTypeID = :licenseTypeID
    """)
    suspend fun getMockExamCount(
        licenseTypeID: Int
    ): Int
}