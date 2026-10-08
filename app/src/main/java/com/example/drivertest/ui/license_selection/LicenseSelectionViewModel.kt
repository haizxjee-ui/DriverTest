package com.example.drivertest.ui.license_selection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.data.local.dao.LicenseTypeDao
import com.example.drivertest.data.repository.LicenseTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenseSelectionViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val licenseTypeRepository: LicenseTypeRepository
) : ViewModel() {

    private val _selectedLicenseId = MutableStateFlow<Int?>(null)
    val selectedLicenseId: StateFlow<Int?> = _selectedLicenseId

    val licenseTypes = licenseTypeRepository.getLicenseTypes()

    init {
        viewModelScope.launch {
            licenseTypes.collect { list ->
                Log.d(
                    "LicenseTest",
                    "Số lượng license: ${list.size}"
                )

                Log.d(
                    "LicenseTest",
                    "Danh sách: ${list.map { it.licenseTypeID }}"
                )
            }
        }
    }

    suspend fun saveSelectedLicense(licenseTypeID: Int) {

        dataStoreManager.setSelectedLicenseTypeID(licenseTypeID)

        Log.d(
            "LicenseSelection",
            "Đã lưu licenseTypeID = $licenseTypeID"
        )
    }
}