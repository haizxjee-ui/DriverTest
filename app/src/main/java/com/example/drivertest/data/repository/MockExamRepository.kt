package com.example.drivertest.data.repository

import androidx.room.withTransaction
import com.example.drivertest.data.local.AppDatabase
import com.example.drivertest.data.local.entity.ExamDetailEntity
import com.example.drivertest.data.local.entity.ExamEntity
import com.example.drivertest.data.local.entity.MockExamEntity
import com.example.drivertest.data.local.entity.MockExamQuestionEntity
import com.example.drivertest.data.local.entity.QuestionEntity
import com.example.drivertest.data.model.MockExamQuestionData
import com.example.drivertest.data.local.entity.WrongQuestionEntity
import com.example.drivertest.data.model.ExamResultQuestionData
import javax.inject.Inject

class MockExamRepository @Inject constructor(
    private val database: AppDatabase
) {

    companion object {
        private const val TOTAL_EXAMS = 20
    }

    /**
     * Lấy bộ đề đã lưu.
     * Nếu chưa có bộ đề hợp lệ thì sinh và lưu 20 đề mới.
     */
    suspend fun getOrCreateMockExams(
        licenseTypeID: Int
    ): List<MockExamEntity> {

        return database.withTransaction {

            val licenseType = database.licenseTypeDao()
                .getLicenseTypeByID(licenseTypeID)
                ?: throw IllegalStateException(
                    "Không tìm thấy hạng bằng đã chọn"
                )

            val rules = database.licenseQuestionRuleDao()
                .getRulesByLicenseTypeID(licenseTypeID)

            require(rules.isNotEmpty()) {
                "Chưa có cấu hình câu hỏi cho hạng bằng này"
            }

            require(rules.sumOf { it.quantity } == licenseType.totalQuestion) {
                "Tổng số câu trong cấu hình không khớp hạng bằng"
            }

            // Lấy ngân hàng câu hỏi theo từng chương.
            val questionsByType = rules.associate { rule ->
                rule.questionTypeID to database.questionDao()
                    .getQuestionsByTypeOnce(rule.questionTypeID)
            }

            // Mỗi chương phải đủ số câu để sinh đề.
            rules.forEach { rule ->
                val questions = questionsByType[rule.questionTypeID].orEmpty()

                require(questions.size >= rule.quantity) {
                    "Chương ${rule.questionTypeID} không đủ câu hỏi"
                }
            }

            // Chỉ lấy câu điểm liệt thuộc các chương được đưa vào đề.
            val criticalQuestions = rules.flatMap { rule ->
                questionsByType[rule.questionTypeID]
                    .orEmpty()
                    .filter { it.isCritical }
            }.distinctBy { it.questionID }

            require(criticalQuestions.isNotEmpty()) {
                "Không tìm thấy câu điểm liệt phù hợp để tạo đề"
            }

            // Kiểm tra bộ đề đã tồn tại và còn đầy đủ dữ liệu.
            val existingExams = database.mockExamDao()
                .getMockExamsByLicenseType(licenseTypeID)

            val existingExamsValid =
                existingExams.size == TOTAL_EXAMS &&
                        existingExams.map { it.examNumber }.toSet() ==
                        (1..TOTAL_EXAMS).toSet() &&
                        existingExams.all { exam ->
                            val savedQuestions = database.mockExamDao()
                                .getQuestionsByMockExamID(exam.mockExamID)

                            savedQuestions.size == licenseType.totalQuestion &&
                                    savedQuestions.any { saved ->
                                        criticalQuestions.any {
                                            it.questionID == saved.questionID
                                        }
                                    }
                        }

            if (existingExamsValid) {
                return@withTransaction existingExams
                    .sortedBy { it.examNumber }
            }

            // Xóa bộ đề chưa đầy đủ hoặc không hợp lệ.
            // Các câu liên kết sẽ được xóa theo ON DELETE CASCADE.
            database.mockExamDao()
                .deleteMockExamsByLicenseType(licenseTypeID)

            // Sinh ngẫu nhiên 20 đề.
            val generatedQuestions = (1..TOTAL_EXAMS).map {
                generateQuestionsForExam(
                    rules = rules,
                    questionsByType = questionsByType,
                    criticalQuestions = criticalQuestions,
                    totalQuestion = licenseType.totalQuestion
                )
            }

            // Lưu 20 đề và lấy ID được Room tạo ra.
            val examsToInsert = (1..TOTAL_EXAMS).map { examNumber ->
                MockExamEntity(
                    licenseTypeID = licenseTypeID,
                    examNumber = examNumber
                )
            }

            val insertedIDs = database.mockExamDao()
                .insertMockExams(examsToInsert)

            // Gắn từng danh sách câu hỏi với đúng đề.
            val questionLinks = generatedQuestions.flatMapIndexed {
                    examIndex, questions ->

                val mockExamID = insertedIDs[examIndex].toInt()

                questions.mapIndexed { questionIndex, question ->
                    MockExamQuestionEntity(
                        mockExamID = mockExamID,
                        questionID = question.questionID,
                        questionOrder = questionIndex + 1
                    )
                }
            }

            database.mockExamDao()
                .insertMockExamQuestions(questionLinks)

            database.mockExamDao()
                .getMockExamsByLicenseType(licenseTypeID)
                .sortedBy { it.examNumber }
        }
    }

    /**
     * Sinh một đề và bắt buộc có ít nhất một câu điểm liệt.
     * Câu điểm liệt được tính vào số lượng câu của chương tương ứng.
     */
    private fun generateQuestionsForExam(
        rules: List<com.example.drivertest.data.local.entity.LicenseQuestionRuleEntity>,
        questionsByType: Map<Int, List<QuestionEntity>>,
        criticalQuestions: List<QuestionEntity>,
        totalQuestion: Int
    ): List<QuestionEntity> {

        val requiredCritical = criticalQuestions.random()

        val selectedQuestions = mutableListOf<QuestionEntity>()

        rules.forEach { rule ->
            val pool = questionsByType[rule.questionTypeID].orEmpty()

            if (rule.questionTypeID == requiredCritical.questionTypeID) {

                val otherQuestions = pool
                    .filter { it.questionID != requiredCritical.questionID }
                    .shuffled()
                    .take(rule.quantity - 1)

                selectedQuestions += requiredCritical
                selectedQuestions += otherQuestions

            } else {
                selectedQuestions += pool
                    .shuffled()
                    .take(rule.quantity)
            }
        }

        require(selectedQuestions.size == totalQuestion) {
            "Số câu sinh ra không đúng cấu hình đề thi"
        }

        require(selectedQuestions.any { it.isCritical }) {
            "Đề thi chưa có câu điểm liệt"
        }

        return selectedQuestions.shuffled()
    }

    suspend fun getMockExamQuestions(
        mockExamID: Int
    ): List<MockExamQuestionData> {

        val mockExam = database.mockExamDao()
            .getMockExamByID(mockExamID)
            ?: throw IllegalStateException("Không tìm thấy đề thi")

        val savedQuestions = database.mockExamDao()
            .getQuestionsByMockExamID(mockExam.mockExamID)
            .sortedBy { it.questionOrder }

        require(savedQuestions.isNotEmpty()) {
            "Đề thi chưa có câu hỏi"
        }

        return savedQuestions.map { savedQuestion ->

            val question = database.questionDao()
                .getQuestionByID(savedQuestion.questionID)
                ?: throw IllegalStateException(
                    "Không tìm thấy câu hỏi ${savedQuestion.questionID}"
                )

            val answers = database.answerDao()
                .getAnswersByQuestionID(question.questionID)

            val images = database.imageDao()
                .getImagesByQuestionID(question.questionID)
                .sortedBy { it.imageOrder }

            MockExamQuestionData(
                question = question,
                answers = answers,
                images = images
            )
        }
    }

    suspend fun getMockExamByID(
        mockExamID: Int
    ): com.example.drivertest.data.local.entity.MockExamEntity? {
        return database.mockExamDao().getMockExamByID(mockExamID)
    }

    suspend fun saveExamResult(
        licenseTypeID: Int,
        startTime: Long,
        endTime: Long,
        correctCount: Int,
        isPassed: Boolean,
        questions: List<MockExamQuestionData>,
        selectedAnswers: Map<Int, Int>
    ): Int {
        return database.withTransaction {
            // 1. Lưu kết quả tổng quan.
            val examID = database.examDao().insertExam(
                ExamEntity(
                    licenseTypeID = licenseTypeID,
                    startTime = startTime,
                    endTime = endTime,
                    status = "COMPLETED",
                    score = correctCount,
                    result = isPassed
                )
            ).toInt()

            // 2. Lưu chi tiết từng câu theo đúng thứ tự đề thi.
            val examDetails = questions.mapIndexed { index, item ->
                val questionID = item.question.questionID
                val selectedAnswerID = selectedAnswers[questionID]

                val isCorrect = item.answers.any { answer ->
                    answer.answerID == selectedAnswerID && answer.isCorrect
                }

                ExamDetailEntity(
                    examID = examID,
                    questionID = questionID,
                    selectedAnswerID = selectedAnswerID,
                    isCorrect = isCorrect,
                    questionOrder = index + 1
                )
            }

            android.util.Log.d(
                "ExamResultDebug",
                "Số câu nhận vào: ${questions.size}"
            )

            android.util.Log.d(
                "ExamResultDebug",
                "Số chi tiết chuẩn bị lưu: ${examDetails.size}"
            )

            database.examDetailDao().insertExamDetails(examDetails)

            val savedDetails = database.examDetailDao()
                .getExamDetailsByExamID(examID)

            android.util.Log.d(
                "ExamResultDebug",
                "Số chi tiết sau khi lưu: ${savedDetails.size}"
            )

// 3. Thêm các câu đã trả lời sai vào danh sách câu sai.
            questions.forEach { item ->
                val questionID = item.question.questionID
                val selectedAnswerID = selectedAnswers[questionID]

                // Không thêm câu bỏ trống vào danh sách câu sai.
                if (selectedAnswerID == null) {
                    return@forEach
                }

                val selectedAnswer = item.answers.firstOrNull { answer ->
                    answer.answerID == selectedAnswerID
                } ?: return@forEach

                if (!selectedAnswer.isCorrect) {
                    database.wrongQuestionDao().insertWrongQuestion(
                        WrongQuestionEntity(questionID = questionID)
                    )
                }
            }

            examID
        }
    }

    suspend fun getExamResultQuestions(
        examID: Int
    ): List<ExamResultQuestionData> {
        val details = database.examDetailDao()
            .getExamDetailsByExamID(examID)

        android.util.Log.d(
            "ExamResultDebug",
            "examID=$examID, số chi tiết trong DB=${details.size}"
        )

        // Giữ nguyên phần code còn lại của bạn

        return details.map { detail ->
            val question = database.questionDao()
                .getQuestionByID(detail.questionID)
                ?: throw IllegalStateException(
                    "Không tìm thấy câu hỏi ${detail.questionID}"
                )

            val answers = database.answerDao()
                .getAnswersByQuestionID(detail.questionID)

            val images = database.imageDao()
                .getImagesByQuestionID(detail.questionID)
                .sortedBy { it.imageOrder }

            ExamResultQuestionData(
                detail = detail,
                question = question,
                answers = answers,
                images = images
            )
        }
    }

    suspend fun getExamResultByID(
        examID: Int
    ): ExamEntity? {
        return database.examDao().getExamByID(examID)
    }

    suspend fun clearAllExamHistory() {
        database.withTransaction {
            database.examDetailDao().deleteAllExamDetails()
            database.examDao().deleteAllExams()
        }
    }
}