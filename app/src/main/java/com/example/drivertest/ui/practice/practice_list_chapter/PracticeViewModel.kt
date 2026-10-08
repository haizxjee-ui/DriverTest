package com.example.drivertest.ui.practice.practice_list_chapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertest.data.repository.PracticeRepository
import com.example.drivertest.data.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val repository: PracticeRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _chapters =
        MutableStateFlow<List<ChapterUiModel>>(emptyList())

    val chapters: StateFlow<List<ChapterUiModel>> =
        _chapters.asStateFlow()

    init {
        refreshChapters()
    }

    fun refreshChapters() {

        viewModelScope.launch {

            val totalCriticalQuestions =
                questionRepository
                    .getCriticalQuestionCount()
                    .first()

            val questionTypes =
                repository
                    .getAllQuestionTypes()
                    .first()

            val chapters =
                questionTypes.mapIndexed { index, questionType ->

                    val chapterNumber = index + 1

                    val totalQuestions =
                        if (chapterNumber == 1) {

                            totalCriticalQuestions

                        } else {

                            repository
                                .getQuestionCount(
                                    questionType.questionTypeID
                                )
                                .first()
                        }

                    val answeredQuestions =
                        if (chapterNumber == 1) {

                            repository
                                .getAnsweredCriticalQuestionCount()
                                .first()

                        } else {

                            repository.getAnsweredQuestionCount(
                                chapterNumber,
                                questionType.questionTypeID
                            )
                                .first()
                        }

                    val criticalQuestionCount =
                        if (chapterNumber == 1) {

                            totalCriticalQuestions

                        } else {

                            repository
                                .getCriticalQuestionCount(
                                    questionType.questionTypeID
                                )
                                .first()
                        }

                    val progressPercent =
                        if (totalQuestions > 0) {

                            answeredQuestions * 100 / totalQuestions

                        } else {
                            0
                        }

                    ChapterUiModel(
                        questionTypeID =
                            questionType.questionTypeID,

                        chapterNumber =
                            chapterNumber,

                        title =
                            questionType.name,

                        totalQuestions =
                            totalQuestions,

                        answeredQuestions =
                            answeredQuestions,

                        progressPercent =
                            progressPercent,

                        criticalQuestionCount =
                            criticalQuestionCount
                    )
                }

            _chapters.value = chapters
        }
    }

    fun resetChapter(
        chapterNumber: Int
    ) {

        viewModelScope.launch {

            repository.resetChapter(
                chapterNumber
            )

            refreshChapters()
        }
    }
}