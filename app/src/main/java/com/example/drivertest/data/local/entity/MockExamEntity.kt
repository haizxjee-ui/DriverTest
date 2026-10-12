package com.example.drivertest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mock_exams",
    foreignKeys = [
        ForeignKey(
            entity = LicenseTypeEntity::class,
            parentColumns = ["licenseTypeID"],
            childColumns = ["licenseTypeID"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["licenseTypeID", "examNumber"], unique = true)
    ]
)
data class MockExamEntity(
    @PrimaryKey(autoGenerate = true)
    val mockExamID: Int = 0,

    val licenseTypeID: Int,

    val examNumber: Int
)