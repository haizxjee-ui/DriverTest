package com.example.drivertest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_types")
data class QuestionTypeEntity(
    @PrimaryKey val questionTypeID: Int,
    val name: String
)
