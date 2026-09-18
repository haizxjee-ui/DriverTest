package com.example.drivertest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exams",
    foreignKeys = [
        ForeignKey(
            entity = LicenseTypeEntity::class,
            parentColumns = ["licenseTypeID"],
            childColumns = ["licenseTypeID"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val examID: Int = 0,
    val licenseTypeID: Int,
    val startTime: Long,
    val endTime: Long,
    val status: String,
    val score: Int,
    val result: Boolean
)
