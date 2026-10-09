package com.example.drivertest.ui.practice.practice_wrong_question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertest.data.model.PracticeQuestion
import com.example.drivertest.data.repository.QuestionRepository
import com.example.drivertest.data.repository.WrongQuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WrongQuestionAnswerState(
    val selectedAnswer: String,
    val isCorrect: Boolean
)

@HiltViewModel
class WrongQuestionViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val wrongQuestionRepository: WrongQuestionRepository
) : ViewModel() {

    private val _questions =
        MutableStateFlow<List<PracticeQuestion>>(emptyList())
    val questions: StateFlow<List<PracticeQuestion>> =
        _questions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _answerStates =
        MutableStateFlow<Map<Int, WrongQuestionAnswerState>>(emptyMap())
    val answerStates: StateFlow<Map<Int, WrongQuestionAnswerState>> =
        _answerStates.asStateFlow()

    // Những câu đã thực sự được trả lời trong phiên hiện tại.
    private val answeredQuestionIDs = mutableSetOf<Int>()

    fun loadQuestions() {
        if (_isLoading.value || _questions.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val questionIDs =
                    wrongQuestionRepository.getAllWrongQuestionIDs()

                val loadedQuestions = questionIDs.mapNotNull { questionID ->
                    questionRepository.getPracticeQuestion(questionID)
                }

                _questions.value = loadedQuestions
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectAnswer(position: Int) {
        val currentQuestion =
            getCurrentQuestion() ?: return

        val answer = currentQuestion.answers.getOrNull(position)
            ?: return

        val questionID = currentQuestion.question.questionID

        // Không cho trả lời lại câu đã trả lời trong phiên này.
        if (questionID in answeredQuestionIDs) return

        val labels = listOf("A", "B", "C", "D", "E")
        val selectedAnswer = labels.getOrNull(position) ?: return

        answeredQuestionIDs.add(questionID)

        _answerStates.value = _answerStates.value + (
                questionID to WrongQuestionAnswerState(
                    selectedAnswer = selectedAnswer,
                    isCorrect = answer.isCorrect
                )
                )
    }

    fun getAnswerState(questionID: Int): WrongQuestionAnswerState? =
        _answerStates.value[questionID]

    fun nextQuestion() {
        if (_currentIndex.value < _questions.value.lastIndex) {
            _currentIndex.value++
        }
    }

    fun previousQuestion() {
        if (_currentIndex.value > 0) {
            _currentIndex.value--
        }
    }

    fun getCurrentQuestion(): PracticeQuestion? =
        _questions.value.getOrNull(_currentIndex.value)

    fun isFirstQuestion(): Boolean =
        _currentIndex.value == 0

    fun isLastQuestion(): Boolean =
        _currentIndex.value >= _questions.value.lastIndex

    fun getCurrentQuestionNumber(): Int =
        _currentIndex.value + 1

    fun getTotalQuestionCount(): Int =
        _questions.value.size

    fun removeAnsweredQuestions(onComplete: () -> Unit) {
        viewModelScope.launch {
            wrongQuestionRepository.removeWrongQuestions(
                answeredQuestionIDs.toList()
            )

            onComplete()
        }
    }
}