package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drivertest.data.local.entity.QuestionTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionTypes(types: List<QuestionTypeEntity>)

    @Query("SELECT * FROM question_types")
    fun getAllQuestionTypes(): Flow<List<QuestionTypeEntity>>

    @Query("SELECT * FROM question_types WHERE questionTypeID = :questionTypeID")
    suspend fun getQuestionTypeByID(questionTypeID: Int): QuestionTypeEntity?

    @Query("DELETE FROM question_types")
    suspend fun deleteAllQuestionTypes()
}