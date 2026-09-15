package com.example.drivertest.data.remote.dto

data class QuestionDto(
    val id: Int,
    val question: String,
    val option: OptionDto,
    val image: ImageDto,
    val answer: String,
    val suggest: String?,
    val questionType: Int,
    val critical: Boolean
)
