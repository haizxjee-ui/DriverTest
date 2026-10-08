package com.example.drivertest.data.repository

import com.example.drivertest.data.local.AppDatabase
import com.example.drivertest.data.local.entity.LicenseTypeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LicenseTypeRepository @Inject constructor(
    private val database: AppDatabase
) {
    fun getLicenseTypes(): Flow<List<LicenseTypeEntity>> {
        return database.licenseTypeDao().getAllLicenseTypes()
    }
}