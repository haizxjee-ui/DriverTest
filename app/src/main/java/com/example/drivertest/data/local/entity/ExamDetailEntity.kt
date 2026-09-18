package com.example.drivertest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exam_details",
    foreignKeys = [
        ForeignKey(
            entity = ExamEntity::class,
            parentColumns = ["examID"],
            childColumns = ["examID"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["questionID"],
            childColumns = ["questionID"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AnswerEntity::class,
            parentColumns = ["answerID"],
            childColumns = ["selectedAnswerID"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class ExamDetailEntity(
    @PrimaryKey(autoGenerate = true) val examDetailID: Int = 0,
    val examID: Int,
    val questionID: Int,
    val selectedAnswerID: Int,
    val isCorrect: Boolean,
    val questionOrder: Int
)
