package com.example.drivertest.data.model

import com.example.drivertest.data.local.entity.AnswerEntity
import com.example.drivertest.data.local.entity.ExamDetailEntity
import com.example.drivertest.data.local.entity.ImageEntity
import com.example.drivertest.data.local.entity.QuestionEntity

data class ExamResultQuestionData(
    val detail: ExamDetailEntity,
    val question: QuestionEntity,
    val answers: List<AnswerEntity>,
    val images: List<ImageEntity>
)