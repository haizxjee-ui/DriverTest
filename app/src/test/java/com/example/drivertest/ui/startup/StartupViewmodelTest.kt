package com.example.drivertest.ui.startup

import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.data.repository.QuestionRepository
import com.example.drivertest.data.seed.DatabaseSeeder
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class StartupViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var connectivityChecker: ConnectivityChecker
    private lateinit var questionRepository: QuestionRepository
    private lateinit var databaseSeeder: DatabaseSeeder

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        dataStoreManager = mockk()
        connectivityChecker = mockk()
        questionRepository = mockk()
        databaseSeeder = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initializeApp should return Success when data is initialized and license is selected`() =
        runTest(testDispatcher) {

            coEvery { databaseSeeder.seed() } just Runs

            every {
                dataStoreManager.isDataInitialized
            } returns flowOf(true)

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(1)

            val viewModel = StartupViewModel(
                dataStoreManager,
                connectivityChecker,
                questionRepository,
                databaseSeeder
            )

            advanceUntilIdle()

            assertEquals(
                StartupState.Success,
                viewModel.state.value
            )
        }

    @Test
    fun `initializeApp should return NeedLicenseSelection when data is initialized but license is not selected`() =
        runTest(testDispatcher) {

            // Arrange
            coEvery { databaseSeeder.seed() } just Runs

            every {
                dataStoreManager.isDataInitialized
            } returns flowOf(true)

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(null)

            // Act
            val viewModel = StartupViewModel(
                dataStoreManager,
                connectivityChecker,
                questionRepository,
                databaseSeeder
            )

            advanceUntilIdle()

            // Assert
            assertEquals(
                StartupState.NeedLicenseSelection,
                viewModel.state.value
            )
        }

    @Test
    fun `initializeApp should return Error when data is not initialized and network is unavailable`() =
        runTest(testDispatcher) {

            // Arrange
            coEvery { databaseSeeder.seed() } just Runs

            every {
                dataStoreManager.isDataInitialized
            } returns flowOf(false)

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(null)

            every {
                connectivityChecker.isNetworkAvailable()
            } returns false

            // Act
            val viewModel = StartupViewModel(
                dataStoreManager,
                connectivityChecker,
                questionRepository,
                databaseSeeder
            )

            advanceUntilIdle()

            // Assert
            assertEquals(
                StartupState.Error("Không có kết nối Internet."),
                viewModel.state.value
            )
        }

    @Test
    fun `initializeApp should return Success when remote data is initialized and license is selected`() =
        runTest(testDispatcher) {

            // Arrange
            coEvery { databaseSeeder.seed() } just Runs

            every {
                dataStoreManager.isDataInitialized
            } returns flowOf(false)

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(1)

            every {
                connectivityChecker.isNetworkAvailable()
            } returns true

            coEvery {
                questionRepository.initializeRemoteData()
            } just Runs

            // Act
            val viewModel = StartupViewModel(
                dataStoreManager,
                connectivityChecker,
                questionRepository,
                databaseSeeder
            )

            advanceUntilIdle()

            // Assert
            assertEquals(
                StartupState.Success,
                viewModel.state.value
            )
        }

    @Test
    fun `initializeApp should return NeedLicenseSelection when remote data is initialized but license is not selected`() =
        runTest(testDispatcher) {

            // Arrange
            coEvery { databaseSeeder.seed() } just Runs

            every {
                dataStoreManager.isDataInitialized
            } returns flowOf(false)

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(null)

            every {
                connectivityChecker.isNetworkAvailable()
            } returns true

            coEvery {
                questionRepository.initializeRemoteData()
            } just Runs

            // Act
            val viewModel = StartupViewModel(
                dataStoreManager,
                connectivityChecker,
                questionRepository,
                databaseSeeder
            )

            advanceUntilIdle()

            // Assert
            assertEquals(
                StartupState.NeedLicenseSelection,
                viewModel.state.value
            )
        }

    @Test
    fun `initializeApp should return Error when repository throws IOException`() =
        runTest(testDispatcher) {

            // Arrange
            coEvery { databaseSeeder.seed() } just Runs

            every {
                dataStoreManager.isDataInitialized
            } returns flowOf(false)

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(null)

            every {
                connectivityChecker.isNetworkAvailable()
            } returns true

            coEvery {
                questionRepository.initializeRemoteData()
            } throws IOException("Network error")

            // Act
            val viewModel = StartupViewModel(
                dataStoreManager,
                connectivityChecker,
                questionRepository,
                databaseSeeder
            )

            advanceUntilIdle()

            // Assert
            assertEquals(
                StartupState.Error("Không thể kết nối đến máy chủ."),
                viewModel.state.value
            )
        }

    @Test
    fun `initializeApp should return Error when database seeder throws Exception`() =
        runTest(testDispatcher) {

            // Arrange
            coEvery {
                databaseSeeder.seed()
            } throws Exception("Seed failed")

            // Act
            val viewModel = StartupViewModel(
                dataStoreManager,
                connectivityChecker,
                questionRepository,
                databaseSeeder
            )

            advanceUntilIdle()

            // Assert
            assertEquals(
                StartupState.Error("Seed failed"),
                viewModel.state.value
            )
        }
}