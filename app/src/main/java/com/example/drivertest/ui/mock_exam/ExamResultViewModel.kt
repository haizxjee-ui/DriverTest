package com.example.drivertest.ui.mock_exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertest.data.local.entity.ExamEntity
import com.example.drivertest.data.model.ExamResultQuestionData
import com.example.drivertest.data.repository.MockExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExamResultUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val exam: ExamEntity? = null,
    val questions: List<ExamResultQuestionData> = emptyList()
)

@HiltViewModel
class ExamResultViewModel @Inject constructor(
    private val mockExamRepository: MockExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamResultUiState())
    val uiState: StateFlow<ExamResultUiState> = _uiState.asStateFlow()

    private var loadedExamID: Int? = null

    fun loadResult(examID: Int) {
        if (loadedExamID == examID) return
        loadedExamID = examID

        viewModelScope.launch {
            _uiState.value = ExamResultUiState(isLoading = true)

            try {
                val exam = mockExamRepository.getExamResultByID(examID)
                    ?: throw IllegalStateException("Không tìm thấy kết quả thi")

                val questions =
                    mockExamRepository.getExamResultQuestions(examID)

                _uiState.value = ExamResultUiState(
                    isLoading = false,
                    exam = exam,
                    questions = questions
                )
            } catch (e: Exception) {
                loadedExamID = null

                _uiState.value = ExamResultUiState(
                    isLoading = false,
                    errorMessage = e.message
                        ?: "Không thể tải kết quả thi"
                )
            }
        }
    }
}