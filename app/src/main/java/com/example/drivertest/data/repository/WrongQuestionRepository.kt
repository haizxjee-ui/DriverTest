package com.example.drivertest.data.repository

import com.example.drivertest.data.local.dao.WrongQuestionDao
import com.example.drivertest.data.local.entity.WrongQuestionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WrongQuestionRepository @Inject constructor(
    private val wrongQuestionDao: WrongQuestionDao
) {

    // Lấy danh sách câu hỏi sai
    fun getAllWrongQuestions(): Flow<List<WrongQuestionEntity>> {
        return wrongQuestionDao.getAllWrongQuestions()
    }

    // Thêm câu hỏi sai, không thêm trùng
    suspend fun addWrongQuestion(questionID: Int) {
        wrongQuestionDao.insertWrongQuestion(
            WrongQuestionEntity(questionID = questionID)
        )
    }

    // Kiểm tra câu hỏi có trong danh sách câu sai không
    suspend fun isWrongQuestion(questionID: Int): Boolean {
        return wrongQuestionDao.isWrongQuestion(questionID)
    }

    // Xóa một câu hỏi sai
    suspend fun removeWrongQuestion(questionID: Int) {
        wrongQuestionDao.deleteWrongQuestion(questionID)
    }

    // Xóa các câu đã làm lại trong phiên hiện tại
    suspend fun removeWrongQuestions(questionIDs: List<Int>) {
        if (questionIDs.isNotEmpty()) {
            wrongQuestionDao.deleteWrongQuestions(questionIDs)
        }
    }

    suspend fun getAllWrongQuestionIDs(): List<Int> {
        return wrongQuestionDao.getAllWrongQuestions()
            .first()
            .map { it.questionID }
    }
}