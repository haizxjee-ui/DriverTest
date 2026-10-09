package com.example.drivertest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "wrong_questions",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["questionID"],
            childColumns = ["questionID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WrongQuestionEntity(
    @PrimaryKey
    val questionID: Int
)