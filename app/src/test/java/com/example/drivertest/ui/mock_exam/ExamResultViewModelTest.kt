package com.example.drivertest.ui.mock_exam

import com.example.drivertest.data.local.entity.ExamEntity
import com.example.drivertest.data.model.ExamResultQuestionData
import com.example.drivertest.data.repository.MockExamRepository
import com.example.drivertest.ui.mock_exam.exam_result.ExamResultViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExamResultViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockExamRepository: MockExamRepository
    private lateinit var viewModel: ExamResultViewModel

    private val examID = 1

    private val exam = mockk<ExamEntity>()

    private val questionData = mockk<ExamResultQuestionData>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockExamRepository = mockk()

        viewModel = ExamResultViewModel(
            mockExamRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // UT-034: Tải kết quả thi thành công
    @Test
    fun loadResult_withValidExamID_returnsSuccess() =
        runTest(testDispatcher) {

            val questions = listOf(questionData)

            coEvery {
                mockExamRepository.getExamResultByID(examID)
            } returns exam

            coEvery {
                mockExamRepository.getExamResultQuestions(examID)
            } returns questions

            viewModel.loadResult(examID)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertEquals(null, state.errorMessage)
            assertSame(exam, state.exam)
            assertEquals(questions, state.questions)
        }

    // UT-035: Kết quả thi không có câu hỏi
    @Test
    fun loadResult_withEmptyQuestions_returnsSuccess() =
        runTest(testDispatcher) {

            coEvery {
                mockExamRepository.getExamResultByID(examID)
            } returns exam

            coEvery {
                mockExamRepository.getExamResultQuestions(examID)
            } returns emptyList()

            viewModel.loadResult(examID)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertSame(exam, state.exam)
            assertTrue(state.questions.isEmpty())
            assertEquals(null, state.errorMessage)
        }

    // UT-036: Không tìm thấy kết quả thi
    @Test
    fun loadResult_whenExamNotFound_returnsError() =
        runTest(testDispatcher) {

            coEvery {
                mockExamRepository.getExamResultByID(examID)
            } returns null

            viewModel.loadResult(examID)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertEquals(
                "Không tìm thấy kết quả thi",
                state.errorMessage
            )
            assertEquals(null, state.exam)
            assertTrue(state.questions.isEmpty())

            coVerify(exactly = 0) {
                mockExamRepository.getExamResultQuestions(examID)
            }
        }

    // UT-037: Lỗi khi tải thông tin kết quả thi
    @Test
    fun loadResult_whenGettingExamThrows_returnsError() =
        runTest(testDispatcher) {

            coEvery {
                mockExamRepository.getExamResultByID(examID)
            } throws Exception("Database error")

            viewModel.loadResult(examID)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertEquals("Database error", state.errorMessage)
            assertEquals(null, state.exam)
        }

    // UT-038: Lỗi khi tải danh sách câu hỏi
    @Test
    fun loadResult_whenGettingQuestionsThrows_returnsError() =
        runTest(testDispatcher) {

            coEvery {
                mockExamRepository.getExamResultByID(examID)
            } returns exam

            coEvery {
                mockExamRepository.getExamResultQuestions(examID)
            } throws Exception("Question loading error")

            viewModel.loadResult(examID)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertEquals(
                "Question loading error",
                state.errorMessage
            )
            assertEquals(null, state.exam)
        }

    // UT-039: Không tải lại khi gọi cùng examID
    @Test
    fun loadResult_withSameExamID_doesNotLoadAgain() =
        runTest(testDispatcher) {

            coEvery {
                mockExamRepository.getExamResultByID(examID)
            } returns exam

            coEvery {
                mockExamRepository.getExamResultQuestions(examID)
            } returns listOf(questionData)

            viewModel.loadResult(examID)
            viewModel.loadResult(examID)

            advanceUntilIdle()

            coVerify(exactly = 1) {
                mockExamRepository.getExamResultByID(examID)
            }

            coVerify(exactly = 1) {
                mockExamRepository.getExamResultQuestions(examID)
            }
        }

    // UT-040: Cho phép tải lại sau khi lần tải trước thất bại
    @Test
    fun loadResult_afterFailure_allowsRetry() =
        runTest(testDispatcher) {

            coEvery {
                mockExamRepository.getExamResultByID(examID)
            } returns null andThen exam

            coEvery {
                mockExamRepository.getExamResultQuestions(examID)
            } returns listOf(questionData)

            viewModel.loadResult(examID)

            advanceUntilIdle()

            assertTrue(
                viewModel.uiState.value.errorMessage != null
            )

            viewModel.loadResult(examID)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertFalse(state.isLoading)
            assertEquals(null, state.errorMessage)
            assertSame(exam, state.exam)

            coVerify(exactly = 2) {
                mockExamRepository.getExamResultByID(examID)
            }
        }

    // UT-041: Kiểm tra trạng thái Loading
    @Test
    fun loadResult_whileRepositoryIsLoading_keepsLoadingState() =
        runTest(testDispatcher) {

            val deferredExam = CompletableDeferred<ExamEntity?>()

            coEvery {
                mockExamRepository.getExamResultByID(examID)
            } coAnswers {
                deferredExam.await()
            }

            viewModel.loadResult(examID)

            runCurrent()

            assertTrue(viewModel.uiState.value.isLoading)

            deferredExam.complete(exam)

            coEvery {
                mockExamRepository.getExamResultQuestions(examID)
            } returns listOf(questionData)

            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertSame(exam, viewModel.uiState.value.exam)
        }
}