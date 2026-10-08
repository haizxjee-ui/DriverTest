package com.example.drivertest.data.local.dao

import UserQuestionProgressEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface UserQuestionProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(
        progress: UserQuestionProgressEntity
    )

    @Query("""
    SELECT *
    FROM user_question_progress
    WHERE chapterNumber = :chapterNumber
    AND questionID = :questionID
""")
    suspend fun getProgressByQuestionID(
        chapterNumber: Int,
        questionID: Int
    ): UserQuestionProgressEntity?

    @Query("""
        SELECT *
        FROM user_question_progress
        WHERE questionID IN (:questionIDs)
    """)
    suspend fun getProgressByQuestionIDs(
        questionIDs: List<Int>
    ): List<UserQuestionProgressEntity>

    @Query("""
        DELETE FROM user_question_progress
        WHERE questionID = :questionID
    """)
    suspend fun deleteProgressByQuestionID(
        questionID: Int
    )

    @Query("DELETE FROM user_question_progress")
    suspend fun deleteAllProgress()

    @Query("""
    SELECT COUNT(*)
    FROM user_question_progress
    INNER JOIN questions
        ON user_question_progress.questionID = questions.questionID
    WHERE user_question_progress.chapterNumber = :chapterNumber
    AND questions.questionTypeID = :questionTypeID
    AND user_question_progress.isAnswered = 1
""")
    fun getAnsweredQuestionCountByType(
        chapterNumber: Int,
        questionTypeID: Int
    ): Flow<Int>

    @Query("""
    SELECT COUNT(*)
    FROM user_question_progress
    INNER JOIN questions
        ON user_question_progress.questionID = questions.questionID
    WHERE user_question_progress.chapterNumber = 1
    AND questions.isCritical = 1
    AND user_question_progress.isAnswered = 1
""")
    fun getAnsweredCriticalQuestionCount(): Flow<Int>

    @Query("""
    DELETE FROM user_question_progress
    WHERE chapterNumber = :chapterNumber
""")
    suspend fun deleteProgressByChapter(
        chapterNumber: Int
    )
}