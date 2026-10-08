package com.example.drivertest.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.data.local.AppDatabase
import com.example.drivertest.data.local.entity.AnswerEntity
import com.example.drivertest.data.local.entity.ImageEntity
import com.example.drivertest.data.local.entity.QuestionEntity
import com.example.drivertest.data.remote.DriverApiService
import com.example.drivertest.data.toAnswerEntities
import com.example.drivertest.data.toEntity
import com.example.drivertest.data.toImageEntities
import com.example.drivertest.data.toQuestionEntity
import com.example.drivertest.data.model.PracticeQuestion
import com.example.drivertest.utils.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val apiService: DriverApiService,
    private val database: AppDatabase,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun initializeRemoteData() {

        val isInitialized = dataStoreManager.isDataInitialized.first()

        if (isInitialized) {
            return
        }

        // ==========================
        // 1. Gọi API
        // ==========================

        val questions = apiService.getQuestions().question
        val questionTypes = apiService.getQuestionTypes().types

        Log.d(
            "REPOSITORY",
            "API trả về ${questions.size} câu hỏi"
        )

        Log.d(
            "REPOSITORY",
            "API trả về ${questionTypes.size} loại câu hỏi"
        )

        // ==========================
        // 2. Convert DTO → Entity
        // ==========================

        val questionTypeEntities = questionTypes.map {
            it.toEntity()
        }

        val questionEntities = questions.map {
            it.toQuestionEntity()
        }

        val answerEntities = questions.flatMap {
            it.toAnswerEntities()
        }

        val imageEntities = questions.flatMap {
            it.toImageEntities()
        }

        // ==========================
        // 3. Kiểm tra questionType
        // ==========================

        val questionTypeIDs = questionTypeEntities
            .map { it.questionTypeID }
            .toSet()

        val invalidQuestions = questionEntities.filter {
            it.questionTypeID !in questionTypeIDs
        }

        Log.d(
            "REPOSITORY",
            "Question types: $questionTypeIDs"
        )

        Log.d(
            "REPOSITORY",
            "Total questions: ${questionEntities.size}"
        )

        Log.d(
            "REPOSITORY",
            "Invalid questions: ${invalidQuestions.size}"
        )

        require(invalidQuestions.isEmpty()) {
            "API chứa questionType không hợp lệ"
        }

        // ==========================
        // 4. Kiểm tra số lượng câu hỏi
        // ==========================

        require(
            questionEntities.size ==
                    AppConstants.EXPECTED_QUESTION_COUNT
        ) {
            "API không trả về đủ " +
                    "${AppConstants.EXPECTED_QUESTION_COUNT} câu hỏi"
        }

        // ==========================
        // 5. Lưu vào Room
        // ==========================

        database.withTransaction {

            // Xóa dữ liệu câu hỏi cũ
            database.questionTypeDao().deleteAllQuestionTypes()
            database.questionDao().deleteAllQuestions()
            database.answerDao().deleteAllAnswers()
            database.imageDao().deleteAllImages()

            // Thêm dữ liệu mới
            database.questionTypeDao().insertQuestionTypes(questionTypeEntities)
            database.questionDao().insertQuestions(questionEntities)
            database.answerDao().insertAnswers(answerEntities)
            database.imageDao().insertImages(imageEntities)
        }
        // 3. Chỉ đánh dấu đã khởi tạo sau khi Transaction thành công
        dataStoreManager.setDataInitialized(true)

        Log.d(
            "REPOSITORY",
            "Đã lưu ${questionEntities.size} câu hỏi vào Room"
        )
    }

    fun getAllQuestionTypes() =
        database.questionTypeDao().getAllQuestionTypes()

    fun getCriticalQuestionCount(): Flow<Int> {
        return database.questionDao().getTotalCriticalQuestionCount()
    }

    fun getCriticalQuestions(): Flow<List<QuestionEntity>> {
        return database
            .questionDao()
            .getCriticalQuestions()
    }

    fun getQuestionsByType(
        questionTypeID: Int
    ): Flow<List<QuestionEntity>> {
        return database
            .questionDao()
            .getQuestionsByType(questionTypeID)
    }

    suspend fun getAnswersByQuestionID(
        questionID: Int
    ): List<AnswerEntity> {

        return database
            .answerDao()
            .getAnswersByQuestionID(questionID)
    }

    suspend fun getImagesByQuestionID(
        questionID: Int
    ): List<ImageEntity> {

        return database
            .imageDao()
            .getImagesByQuestionID(questionID)
    }

    suspend fun getPracticeQuestion(
        questionID: Int
    ): PracticeQuestion? {

        val question =
            database
                .questionDao()
                .getQuestionByID(questionID)
                ?: return null

        val answers =
            database
                .answerDao()
                .getAnswersByQuestionID(questionID)

        val images =
            database
                .imageDao()
                .getImagesByQuestionID(questionID)

        return PracticeQuestion(
            question = question,
            answers = answers,
            images = images
        )
    }

}