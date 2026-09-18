package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drivertest.data.local.entity.AnswerEntity

@Dao
interface AnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AnswerEntity>): List<Long>

    @Query("SELECT * FROM answers WHERE questionID = :questionID")
    suspend fun getAnswersByQuestionID(questionID: Int): List<AnswerEntity>

    @Query("SELECT * FROM answers WHERE answerID = :answerID")
    suspend fun getAnswerByID(answerID: Int): AnswerEntity?

    @Query("DELETE FROM answers")
    suspend fun deleteAllAnswers()
}