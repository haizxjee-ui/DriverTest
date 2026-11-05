package com.example.drivertest.ui.mock_exam

import com.example.drivertest.data.DataStoreManager
import com.example.drivertest.data.local.entity.MockExamEntity
import com.example.drivertest.data.repository.MockExamRepository
import com.example.drivertest.ui.mock_exam.mock_list_exam.MockExamListUiState
import com.example.drivertest.ui.mock_exam.mock_list_exam.MockExamViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MockExamViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockExamRepository: MockExamRepository
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var viewModel: MockExamViewModel

    private val licenseTypeID = 1

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockExamRepository = mockk()
        dataStoreManager = mockk()

        viewModel = MockExamViewModel(
            mockExamRepository,
            dataStoreManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // UT-029: Tải danh sách đề thi thành công
    @Test
    fun loadExams_withValidLicense_returnsSuccess() = runTest(testDispatcher) {

        val exams = List(20) { index ->
            MockExamEntity(
                mockExamID = index + 1,
                licenseTypeID = licenseTypeID,
                examNumber = index + 1
            )
        }

        every {
            dataStoreManager.selectedLicenseTypeID
        } returns flowOf(licenseTypeID)

        coEvery {
            mockExamRepository.getOrCreateMockExams(licenseTypeID)
        } returns exams

        viewModel.loadExams()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertTrue(state is MockExamListUiState.Success)

        state as MockExamListUiState.Success

        assertEquals(exams, state.exams)
        assertEquals(20, state.exams.size)

        coVerify(exactly = 1) {
            mockExamRepository.getOrCreateMockExams(licenseTypeID)
        }
    }

    // UT-030: Chưa chọn hạng bằng lái
    @Test
    fun loadExams_withoutSelectedLicense_returnsError() =
        runTest(testDispatcher) {

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(null)

            viewModel.loadExams()

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(state is MockExamListUiState.Error)

            state as MockExamListUiState.Error

            assertEquals(
                "Bạn chưa chọn hạng bằng lái",
                state.message
            )

            coVerify(exactly = 0) {
                mockExamRepository.getOrCreateMockExams(any())
            }
        }

    // UT-031: Repository trả về danh sách rỗng
    @Test
    fun loadExams_withEmptyList_returnsSuccess() =
        runTest(testDispatcher) {

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(licenseTypeID)

            coEvery {
                mockExamRepository.getOrCreateMockExams(licenseTypeID)
            } returns emptyList()

            viewModel.loadExams()

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(state is MockExamListUiState.Success)

            state as MockExamListUiState.Success

            assertTrue(state.exams.isEmpty())
        }

    // UT-032: Repository phát sinh ngoại lệ
    @Test
    fun loadExams_whenRepositoryThrows_returnsError() =
        runTest(testDispatcher) {

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(licenseTypeID)

            coEvery {
                mockExamRepository.getOrCreateMockExams(licenseTypeID)
            } throws Exception("Database error")

            viewModel.loadExams()

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(state is MockExamListUiState.Error)

            state as MockExamListUiState.Error

            assertEquals("Database error", state.message)
        }

    // UT-033: Kiểm tra trạng thái Loading
    @Test
    fun loadExams_whileRepositoryIsLoading_keepsLoadingState() =
        runTest(testDispatcher) {

            val deferredExams =
                CompletableDeferred<List<MockExamEntity>>()

            every {
                dataStoreManager.selectedLicenseTypeID
            } returns flowOf(licenseTypeID)

            coEvery {
                mockExamRepository.getOrCreateMockExams(licenseTypeID)
            } coAnswers {
                deferredExams.await()
            }

            viewModel.loadExams()

            // Chạy coroutine đến khi repository bắt đầu chờ kết quả.
            runCurrent()

            assertTrue(
                viewModel.uiState.value is MockExamListUiState.Loading
            )

            // Cho repository trả về dữ liệu.
            deferredExams.complete(
                listOf(
                    MockExamEntity(
                        mockExamID = 1,
                        licenseTypeID = licenseTypeID,
                        examNumber = 1
                    )
                )
            )

            advanceUntilIdle()

            assertTrue(
                viewModel.uiState.value is MockExamListUiState.Success
            )
        }
}