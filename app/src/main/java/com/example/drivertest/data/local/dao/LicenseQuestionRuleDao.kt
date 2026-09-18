package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drivertest.data.local.entity.LicenseQuestionRuleEntity

@Dao
interface LicenseQuestionRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<LicenseQuestionRuleEntity>)

    @Query(
        "SELECT * FROM license_question_rules " +
                "WHERE licenseTypeID = :licenseTypeID"
    )
    suspend fun getRulesByLicenseTypeID(licenseTypeID: Int): List<LicenseQuestionRuleEntity>

    @Query(
        "SELECT * FROM license_question_rules " +
                "WHERE licenseTypeID = :licenseTypeID " +
                "AND questionTypeID = :questionTypeID"
    )
    suspend fun getRule(licenseTypeID: Int, questionTypeID: Int): LicenseQuestionRuleEntity?

    @Query("DELETE FROM license_question_rules")
    suspend fun deleteAllRules()
}