package com.example.drivertest.ui.license_selection

import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.data.local.entity.LicenseTypeEntity
import com.example.drivertest.data.repository.LicenseTypeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.Runs
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LicenseSelectionViewModelTest {

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var licenseTypeRepository: LicenseTypeRepository

    private lateinit var viewModel: LicenseSelectionViewModel

    @Before
    fun setup() {
        dataStoreManager = mockk()
        licenseTypeRepository = mockk()

        every {
            licenseTypeRepository.getLicenseTypes()
        } returns flowOf(emptyList())

        viewModel = LicenseSelectionViewModel(
            dataStoreManager,
            licenseTypeRepository
        )
    }

    // UT-008: Lấy danh sách hạng giấy phép
    @Test
    fun getLicenseTypes_returnsLicenseTypesFromRepository() = runTest {
        // Arrange
        val expectedLicenseTypes = listOf(
            LicenseTypeEntity(
                licenseTypeID = 1,
                name = "B",
                examTime = 20,
                totalQuestion = 30,
                passingScore = 27,
                description = "Giấy phép lái xe hạng B"
            ),
            LicenseTypeEntity(
                licenseTypeID = 2,
                name = "C1",
                examTime = 24,
                totalQuestion = 35,
                passingScore = 32,
                description = "Giấy phép lái xe hạng C1"
            )
        )

        every {
            licenseTypeRepository.getLicenseTypes()
        } returns flowOf(expectedLicenseTypes)

        viewModel = LicenseSelectionViewModel(
            dataStoreManager,
            licenseTypeRepository
        )

        // Act
        val actualLicenseTypes = viewModel.licenseTypes.first()

        // Assert
        assertEquals(expectedLicenseTypes, actualLicenseTypes)
    }

    // UT-009: Lấy ID hạng giấy phép đã lưu
    @Test
    fun getSavedLicenseId_whenIdExists_returnsSavedId() = runTest {
        // Arrange
        every {
            dataStoreManager.selectedLicenseTypeID
        } returns flowOf(1)

        // Act
        val result = viewModel.getSavedLicenseId()

        // Assert
        assertEquals(1, result)
    }

    // UT-010: Chưa có ID hạng giấy phép được lưu
    @Test
    fun getSavedLicenseId_whenIdDoesNotExist_returnsNull() = runTest {
        // Arrange
        every {
            dataStoreManager.selectedLicenseTypeID
        } returns flowOf(null)

        // Act
        val result = viewModel.getSavedLicenseId()

        // Assert
        assertEquals(null, result)
    }

    // UT-011: Lưu hạng giấy phép được chọn
    @Test
    fun saveSelectedLicense_savesLicenseIdToDataStore() = runTest {
        // Arrange
        val selectedLicenseId = 2

        coEvery {
            dataStoreManager.setSelectedLicenseTypeID(selectedLicenseId)
        } just Runs

        // Act
        viewModel.saveSelectedLicense(selectedLicenseId)

        // Assert
        coVerify(exactly = 1) {
            dataStoreManager.setSelectedLicenseTypeID(selectedLicenseId)
        }
    }
}