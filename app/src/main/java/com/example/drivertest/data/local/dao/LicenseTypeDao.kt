package com.example.drivertest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.drivertest.data.local.entity.LicenseTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LicenseTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLicenseTypes(licenseTypes: List<LicenseTypeEntity>)

    @Query("SELECT * FROM license_types")
    fun getAllLicenseTypes(): Flow<List<LicenseTypeEntity>>

    @Query("SELECT * FROM license_types WHERE licenseTypeID = :licenseTypeID")
    suspend fun getLicenseTypeByID(licenseTypeID: Int): LicenseTypeEntity?

    @Query("DELETE FROM license_types")
    suspend fun deleteAllLicenseTypes()
}