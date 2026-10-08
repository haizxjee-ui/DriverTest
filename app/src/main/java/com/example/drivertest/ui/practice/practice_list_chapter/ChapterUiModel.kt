package com.example.drivertest.ui.practice.practice_list_chapter

data class ChapterUiModel(
    val questionTypeID: Int,
    val chapterNumber: Int,
    val title: String,
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val progressPercent: Int,
    val criticalQuestionCount: Int
)