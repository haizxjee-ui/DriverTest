package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drivertest.data.local.entity.WrongQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WrongQuestionDao {

    // Thêm câu sai, bỏ qua nếu câu hỏi đã tồn tại
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWrongQuestion(
        wrongQuestion: WrongQuestionEntity
    )

    // Lấy danh sách ID các câu hỏi sai
    @Query("SELECT * FROM wrong_questions")
    fun getAllWrongQuestions(): Flow<List<WrongQuestionEntity>>

    // Kiểm tra câu hỏi đã nằm trong danh sách câu sai chưa
    @Query("SELECT EXISTS(SELECT 1 FROM wrong_questions WHERE questionID = :questionID)")
    suspend fun isWrongQuestion(questionID: Int): Boolean

    // Xóa một câu hỏi khỏi danh sách câu sai
    @Query("DELETE FROM wrong_questions WHERE questionID = :questionID")
    suspend fun deleteWrongQuestion(questionID: Int)

    // Xóa nhiều câu hỏi cùng lúc khi người dùng nhấn Back về Home
    @Query("DELETE FROM wrong_questions WHERE questionID IN (:questionIds)")
    suspend fun deleteWrongQuestions(questionIds: List<Int>)
}