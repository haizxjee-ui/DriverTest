package com.example.drivertest.ui.practice.practice_question

import PracticeProgressEntity
import UserQuestionProgressEntity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertest.data.repository.PracticeRepository
import com.example.drivertest.data.repository.QuestionRepository
import com.example.drivertest.data.repository.WrongQuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeQuestionViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val practiceRepository: PracticeRepository,
    private val wrongQuestionRepository: WrongQuestionRepository
) : ViewModel() {

    private val _question =
        MutableStateFlow<PracticeQuestionUiModel?>(null)

    val question: StateFlow<PracticeQuestionUiModel?> =
        _question.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private var questions =
        emptyList<com.example.drivertest.data.local.entity.QuestionEntity>()

    private var currentQuestionIndex = 0

    private var currentChapterNumber = 0

    private var currentQuestionTypeID = 0

    fun loadQuestion(
        chapterNumber: Int,
        questionTypeID: Int
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                currentChapterNumber =
                    chapterNumber

                currentQuestionTypeID =
                    questionTypeID

                questions = if (chapterNumber == 1) {

                    questionRepository
                        .getCriticalQuestions()
                        .first()

                } else {

                    questionRepository
                        .getQuestionsByType(
                            questionTypeID
                        )
                        .first()
                }

                if (questions.isEmpty()) {

                    _question.value = null

                    return@launch
                }

                val progress =
                    practiceRepository
                        .getPracticeProgress(currentChapterNumber)

                currentQuestionIndex = if (progress != null) {

                    questions.indexOfFirst {
                        it.questionID ==
                                progress.currentQuestionID
                    }.takeIf {
                        it >= 0
                    } ?: 0

                } else {

                    0
                }

                loadCurrentQuestion()

            } finally {

                _isLoading.value = false
            }
        }
    }

    private suspend fun loadCurrentQuestion() {

        val currentQuestion =
            questions[currentQuestionIndex]

        val answers =
            questionRepository
                .getAnswersByQuestionID(
                    currentQuestion.questionID
                )

        val images =
            questionRepository
                .getImagesByQuestionID(
                    currentQuestion.questionID
                )

        val userProgress =
            practiceRepository
                .getQuestionProgress(
                    currentChapterNumber,
                    currentQuestion.questionID
                )

        _question.value =
            PracticeQuestionUiModel(
                question = currentQuestion,
                answers = answers,
                images = images,
                userProgress = userProgress
            )

        saveCurrentQuestionProgress(
            currentQuestion.questionID
        )
    }

    private suspend fun saveCurrentQuestionProgress(
        questionID: Int
    ) {
        practiceRepository
            .savePracticeProgress(
                PracticeProgressEntity(
                    chapterNumber = currentChapterNumber,
                    currentQuestionID = questionID
                )
            )
    }

    fun nextQuestion() {

        viewModelScope.launch {

            if (
                currentQuestionIndex >=
                questions.lastIndex
            ) {
                return@launch
            }

            currentQuestionIndex++

            loadCurrentQuestion()
        }
    }

    fun previousQuestion() {

        viewModelScope.launch {

            if (currentQuestionIndex <= 0) {
                return@launch
            }

            currentQuestionIndex--

            loadCurrentQuestion()
        }
    }

    fun isFirstQuestion(): Boolean {

        return currentQuestionIndex == 0
    }

    fun isLastQuestion(): Boolean {

        return currentQuestionIndex ==
                questions.lastIndex
    }

    fun getCurrentQuestionNumber(): Int {

        return currentQuestionIndex + 1
    }

    fun getTotalQuestionCount(): Int {

        return questions.size
    }

    fun selectAnswer(position: Int) {
        viewModelScope.launch {

            val currentQuestion =
                _question.value?.question
                    ?: return@launch

            val answers =
                _question.value?.answers
                    ?: return@launch

            if (position !in answers.indices) {
                return@launch
            }

            val selectedAnswer = answers[position]

            val answerLabels = listOf(
                "A",
                "B",
                "C",
                "D",
                "E"
            )

            val progress = UserQuestionProgressEntity(
                chapterNumber = currentChapterNumber,
                questionID = currentQuestion.questionID,
                selectedAnswer = answerLabels[position],
                isAnswered = true,
                isCorrect = selectedAnswer.isCorrect
            )

            // Lưu tiến trình trả lời như trước
            practiceRepository.saveQuestionProgress(progress)

            // Nếu trả lời sai, thêm câu hỏi vào danh sách câu sai
            if (!selectedAnswer.isCorrect) {
                wrongQuestionRepository.addWrongQuestion(
                    currentQuestion.questionID
                )
            }
        }
    }
}