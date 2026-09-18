package com.example.drivertest.data.remote

import com.example.drivertest.data.remote.dto.QuestionResponseDto
import com.example.drivertest.data.remote.dto.QuestionTypeResponseDto
import retrofit2.http.GET

interface DriverApiService {

    @GET("resources/braniumapis/driver_test.json")
    suspend fun getQuestions(): QuestionResponseDto

    @GET("resources/braniumapis/test_type.json")
    suspend fun getQuestionTypes(): QuestionTypeResponseDto
}