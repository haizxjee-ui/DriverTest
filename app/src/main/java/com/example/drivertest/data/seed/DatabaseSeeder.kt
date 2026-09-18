package com.example.drivertest.data.seed

import com.example.drivertest.data.local.dao.LicenseQuestionRuleDao
import com.example.drivertest.data.local.dao.LicenseTypeDao
import javax.inject.Inject

class DatabaseSeeder @Inject constructor(
    private val licenseTypeDao: LicenseTypeDao,
    private val licenseQuestionRuleDao: LicenseQuestionRuleDao
) {

    suspend fun seed() {

        val licenseType = licenseTypeDao.getLicenseTypeByID(1)

        if (licenseType == null) {
            licenseTypeDao.insertLicenseTypes(LicenseTypeSeed.data)
            licenseQuestionRuleDao.insertRules(LicenseQuestionRuleSeed.data)
        }
    }
}