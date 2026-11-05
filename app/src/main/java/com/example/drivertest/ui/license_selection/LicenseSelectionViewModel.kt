package com.example.drivertest.ui.license_selection

import androidx.lifecycle.ViewModel
import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.data.repository.LicenseTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class LicenseSelectionViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    licenseTypeRepository: LicenseTypeRepository
) : ViewModel() {

    private var _licenseTypes = licenseTypeRepository.getLicenseTypes()
    val licenseTypes = _licenseTypes

    suspend fun getSavedLicenseId(): Int? {
        return dataStoreManager.selectedLicenseTypeID.first()
    }

    suspend fun saveSelectedLicense(licenseTypeID: Int) {
        dataStoreManager.setSelectedLicenseTypeID(licenseTypeID)
    }
}
