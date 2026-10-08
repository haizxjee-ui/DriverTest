package com.example.drivertest.ui.practice.practice_question

import UserQuestionProgressEntity
import com.example.drivertest.data.local.entity.AnswerEntity
import com.example.drivertest.data.local.entity.ImageEntity
import com.example.drivertest.data.local.entity.QuestionEntity

data class PracticeQuestionUiModel(
    val question: QuestionEntity,
    val answers: List<AnswerEntity>,
    val images: List<ImageEntity>,
    val userProgress: UserQuestionProgressEntity?
)