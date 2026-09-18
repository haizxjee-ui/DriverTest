package com.example.drivertest.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "license_question_rules",
    primaryKeys = ["licenseTypeID", "questionTypeID"]
)
data class LicenseQuestionRuleEntity(
    val licenseTypeID: Int,
    val questionTypeID: Int,
    val quantity: Int
)
