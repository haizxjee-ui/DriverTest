package com.example.drivertest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "mock_exam_questions",
    primaryKeys = ["mockExamID", "questionOrder"],
    foreignKeys = [
        ForeignKey(
            entity = MockExamEntity::class,
            parentColumns = ["mockExamID"],
            childColumns = ["mockExamID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["questionID"],
            childColumns = ["questionID"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["questionID"])
    ]
)
data class MockExamQuestionEntity(
    val mockExamID: Int,
    val questionID: Int,
    val questionOrder: Int
)