package com.example.drivertest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    val id: Int,
    val question: String,
    val option: OptionDto,
    val image: ImageDto?,
    val answer: String,
    val suggest: String?,
    @SerializedName("questionType", alternate = ["QUESTIONTYPE"])
    val questionType: Int,
    val critical: Boolean
)
