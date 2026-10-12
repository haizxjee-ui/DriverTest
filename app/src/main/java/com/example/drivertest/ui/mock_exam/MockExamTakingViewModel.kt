package com.example.drivertest.ui.mock_exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertest.data.local.entity.LicenseTypeEntity
import com.example.drivertest.data.model.MockExamQuestionData
import com.example.drivertest.data.repository.MockExamRepository
import com.example.drivertest.data.local.dao.LicenseTypeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MockExamTakingUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val questions: List<MockExamQuestionData> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val remainingTimeSeconds: Int = 0,
    val isSubmitted: Boolean = false,
    val correctCount: Int = 0,
    val isPassed: Boolean = false,
    val isSavingResult: Boolean = false,
    val savedExamID: Int? = null,
    val saveErrorMessage: String? = null
)

@HiltViewModel
class MockExamTakingViewModel @Inject constructor(
    private val mockExamRepository: MockExamRepository,
    private val licenseTypeDao: LicenseTypeDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(MockExamTakingUiState())
    val uiState: StateFlow<MockExamTakingUiState> = _uiState.asStateFlow()

    private var licenseType: LicenseTypeEntity? = null

    private var examStartTime: Long = 0L

    private var currentMockExamID: Int = -1

    fun loadExam(mockExamID: Int) {
        if (_uiState.value.questions.isNotEmpty()) return

        currentMockExamID = mockExamID

        viewModelScope.launch {
            _uiState.value = MockExamTakingUiState(isLoading = true)

            try {
                val questions =
                    mockExamRepository.getMockExamQuestions(mockExamID)

                val mockExam = mockExamRepository.getMockExamByID(mockExamID)
                    ?: throw IllegalStateException("Không tìm thấy đề thi")

                licenseType = licenseTypeDao.getLicenseTypeByID(
                    mockExam.licenseTypeID
                ) ?: throw IllegalStateException("Không tìm thấy hạng bằng")

                examStartTime = System.currentTimeMillis()

                val timeSeconds = licenseType!!.examTime * 60

                _uiState.value = MockExamTakingUiState(
                    isLoading = false,
                    questions = questions,
                    remainingTimeSeconds = timeSeconds
                )
            } catch (e: Exception) {
                _uiState.value = MockExamTakingUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Không thể tải đề thi"
                )
            }
        }
    }

    fun selectAnswer(questionID: Int, answerID: Int) {
        val state = _uiState.value
        if (state.isSubmitted) return

        _uiState.value = state.copy(
            selectedAnswers = state.selectedAnswers + (questionID to answerID)
        )
    }

    fun goToQuestion(index: Int) {
        val state = _uiState.value
        if (index !in state.questions.indices || state.isSubmitted) return

        _uiState.value = state.copy(currentQuestionIndex = index)
    }

    fun nextQuestion() {
        goToQuestion(_uiState.value.currentQuestionIndex + 1)
    }

    fun previousQuestion() {
        goToQuestion(_uiState.value.currentQuestionIndex - 1)
    }

    fun tickTimer() {
        val state = _uiState.value
        if (
            state.isSubmitted ||
            state.isSavingResult ||
            state.isLoading
        ) return

        if (state.remainingTimeSeconds <= 1) {
            _uiState.value = state.copy(remainingTimeSeconds = 0)
            submitExam()
        } else {
            _uiState.value = state.copy(
                remainingTimeSeconds = state.remainingTimeSeconds - 1
            )
        }
    }

    fun submitExam() {
        val state = _uiState.value

        // Ngăn nộp bài nhiều lần hoặc khi đề chưa tải xong.
        if (
            state.isSubmitted ||
            state.isSavingResult ||
            state.savedExamID != null ||
            state.questions.isEmpty()
        ) {
            return
        }

        val license = licenseType ?: return

        // Tính số câu đúng.
        val correctCount = state.questions.count { item ->
            val selectedAnswerID =
                state.selectedAnswers[item.question.questionID]

            item.answers.any { answer ->
                answer.answerID == selectedAnswerID && answer.isCorrect
            }
        }

        // Nếu sai câu điểm liệt thì không đạt.
        val hasWrongCriticalQuestion = state.questions.any { item ->
            if (!item.question.isCritical) {
                false
            } else {
                val selectedAnswerID =
                    state.selectedAnswers[item.question.questionID]

                item.answers.any { answer ->
                    answer.answerID == selectedAnswerID && !answer.isCorrect
                }
            }
        }

        val isPassed = correctCount >= license.passingScore &&
                !hasWrongCriticalQuestion

        // Đánh dấu đang lưu ngay lập tức để chặn các lần nộp trùng.
        _uiState.value = state.copy(
            isSavingResult = true,
            saveErrorMessage = null,
            correctCount = correctCount,
            isPassed = isPassed
        )

        viewModelScope.launch {
            try {
                val mockExam = mockExamRepository.getMockExamByID(
                    mockExamID = currentMockExamID
                ) ?: throw IllegalStateException("Không tìm thấy đề thi")

                val savedExamID = mockExamRepository.saveExamResult(
                    licenseTypeID = mockExam.licenseTypeID,
                    startTime = examStartTime,
                    endTime = System.currentTimeMillis(),
                    correctCount = correctCount,
                    isPassed = isPassed,
                    questions = state.questions,
                    selectedAnswers = state.selectedAnswers
                )

                _uiState.value = _uiState.value.copy(
                    isSavingResult = false,
                    isSubmitted = true,
                    savedExamID = savedExamID,
                    saveErrorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSavingResult = false,
                    saveErrorMessage = e.message
                        ?: "Không thể lưu kết quả. Vui lòng thử lại."
                )
            }
        }
    }

    fun clearSaveErrorMessage() {
        _uiState.value = _uiState.value.copy(
            saveErrorMessage = null
        )
    }
}