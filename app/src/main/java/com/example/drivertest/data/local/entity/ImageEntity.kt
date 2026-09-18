package com.example.drivertest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "images",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["questionID"],
            childColumns = ["questionID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val imageID: Int = 0,
    val questionID: Int,
    val imageUrl: String,
    val imageOrder: Int
)
