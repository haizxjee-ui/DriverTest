package com.example.drivertest.data.model

import com.example.drivertest.data.local.entity.QuestionEntity

data class MockExam(
    val licenseTypeID: Int,
    val questions: List<QuestionEntity>,
    val durationMinutes: Int,
    val passingScore: Int
)