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
}