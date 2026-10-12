package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drivertest.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE questionID = :questionID")
    suspend fun getQuestionByID(questionID: Int): QuestionEntity?

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Query("""
    SELECT COUNT(*)
    FROM questions
    WHERE questionTypeID = :questionTypeID
""")
    fun getQuestionCountByType(
        questionTypeID: Int
    ): Flow<Int>

    @Query("""
    SELECT COUNT(*)
    FROM questions
    WHERE questionTypeID = :questionTypeID
    AND isCritical = 1
""")
    fun getCriticalQuestionCountByType(
        questionTypeID: Int
    ): Flow<Int>

    @Query("""
    SELECT COUNT(*)
    FROM questions
    WHERE isCritical = 1
""")
    fun getTotalCriticalQuestionCount(): Flow<Int>

    @Query("""
    SELECT *
    FROM questions
    WHERE isCritical = 1
    ORDER BY questionID
""")
    fun getCriticalQuestions(): Flow<List<QuestionEntity>>

    @Query("""
    SELECT *
    FROM questions
    WHERE questionTypeID = :questionTypeID
    ORDER BY questionID
""")
    fun getQuestionsByType(
        questionTypeID: Int
    ): Flow<List<QuestionEntity>>

    @Query("""
    SELECT * FROM questions
    WHERE questionTypeID = :questionTypeID
    ORDER BY questionID
""")
    suspend fun getQuestionsByTypeOnce(
        questionTypeID: Int
    ): List<QuestionEntity>

    @Query("""
    SELECT * FROM questions
    WHERE isCritical = 1
    ORDER BY questionID
""")
    suspend fun getCriticalQuestionsOnce(): List<QuestionEntity>
}