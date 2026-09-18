package com.example.drivertest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "license_types")
data class LicenseTypeEntity(
    @PrimaryKey val licenseTypeID: Int,
    val name: String,
    val examTime: Int,
    val totalQuestion: Int,
    val passingScore: Int,
    val description: String
)
