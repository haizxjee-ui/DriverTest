package com.example.drivertest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = QuestionTypeEntity::class,
            parentColumns = ["questionTypeID"],
            childColumns = ["questionTypeID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuestionEntity(
    @PrimaryKey val questionID: Int,
    val questionTypeID: Int,
    val content: String,
    val explanation: String?,
    val isCritical: Boolean
)
