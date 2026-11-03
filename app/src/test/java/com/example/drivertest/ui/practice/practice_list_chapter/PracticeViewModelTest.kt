package com.example.drivertest.ui.practice.practice_list_chapter

import com.example.drivertest.data.local.entity.QuestionTypeEntity
import com.example.drivertest.data.repository.PracticeRepository
import com.example.drivertest.data.repository.QuestionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.Runs
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.After
import org.junit.Test

class PracticeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: PracticeRepository
    private lateinit var questionRepository: QuestionRepository
    private lateinit var viewModel: PracticeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk()
        questionRepository = mockk()

        // Giá trị mặc định để ViewModel có thể khởi tạo
        every {
            questionRepository.getCriticalQuestionCount()
        } returns flowOf(0)

        every {
            questionRepository.getCriticalQuestionCount()
        } returns flowOf(0)

        every {
            repository.getAllQuestionTypes()
        } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // UT-012: Tải danh sách chương
    @Test
    fun refreshChapters_returnsCorrectChapterList() = runTest(testDispatcher) {

        // Arrange
        val questionTypes = listOf(
            QuestionTypeEntity(2001, "Điểm liệt"),
            QuestionTypeEntity(2002, "Khái niệm và quy tắc")
        )

        every {
            repository.getAllQuestionTypes()
        } returns flowOf(questionTypes)

        every {
            questionRepository.getCriticalQuestionCount()
        } returns flowOf(10)

        every {
            repository.getAnsweredCriticalQuestionCount()
        } returns flowOf(5)

        every {
            repository.getQuestionCount(2002)
        } returns flowOf(20)

        every {
            repository.getAnsweredQuestionCount(2, 2002)
        } returns flowOf(10)

        every {
            repository.getCriticalQuestionCount(2002)
        } returns flowOf(3)

        // Act
        viewModel = PracticeViewModel(repository, questionRepository)
        advanceUntilIdle()

        val result = viewModel.chapters.value

        // Assert
        assertEquals(2, result.size)

        assertEquals(
            ChapterUiModel(2001, 1, "Điểm liệt", 10, 5, 50, 10),
            result[0]
        )

        assertEquals(
            ChapterUiModel(
                2002, 2, "Khái niệm và quy tắc", 20, 10, 50, 3
            ),
            result[1]
        )
    }

    // UT-013: Tính phần trăm tiến độ
    @Test
    fun refreshChapters_calculatesCorrectProgressPercent() =
        runTest(testDispatcher) {

            // Arrange
            every {
                repository.getAllQuestionTypes()
            } returns flowOf(
                listOf(QuestionTypeEntity(2001, "Điểm liệt"))
            )

            every {
                questionRepository.getCriticalQuestionCount()
            } returns flowOf(4)

            every {
                repository.getAnsweredCriticalQuestionCount()
            } returns flowOf(3)

            // Act
            viewModel = PracticeViewModel(repository, questionRepository)
            advanceUntilIdle()

            val result = viewModel.chapters.value.first()

            // Assert
            assertEquals(75, result.progressPercent)
        }

    // UT-014: Chương không có câu hỏi
    @Test
    fun refreshChapters_whenNoQuestions_progressPercentIsZero() =
        runTest(testDispatcher) {

            // Arrange
            every {
                repository.getAllQuestionTypes()
            } returns flowOf(
                listOf(QuestionTypeEntity(2001, "Điểm liệt"))
            )

            every {
                questionRepository.getCriticalQuestionCount()
            } returns flowOf(0)

            every {
                repository.getAnsweredCriticalQuestionCount()
            } returns flowOf(0)

            // Act
            viewModel = PracticeViewModel(repository, questionRepository)
            advanceUntilIdle()

            val result = viewModel.chapters.value.first()

            // Assert
            assertEquals(0, result.totalQuestions)
            assertEquals(0, result.progressPercent)
        }

    // UT-015: Chương 1 sử dụng số lượng câu hỏi điểm liệt
    @Test
    fun refreshChapters_firstChapter_usesCriticalQuestionCount() =
        runTest(testDispatcher) {

            // Arrange
            every {
                repository.getAllQuestionTypes()
            } returns flowOf(
                listOf(QuestionTypeEntity(2001, "Điểm liệt"))
            )

            every {
                questionRepository.getCriticalQuestionCount()
            } returns flowOf(60)

            every {
                repository.getAnsweredCriticalQuestionCount()
            } returns flowOf(15)

            // Act
            viewModel = PracticeViewModel(repository, questionRepository)
            advanceUntilIdle()

            val result = viewModel.chapters.value.first()

            // Assert
            assertEquals(60, result.totalQuestions)
            assertEquals(15, result.answeredQuestions)
            assertEquals(25, result.progressPercent)
            assertEquals(60, result.criticalQuestionCount)

            verify(exactly = 0) {
                repository.getQuestionCount(2001)
            }
        }

    // UT-016: Reset chương
    @Test
    fun resetChapter_callsRepositoryWithCorrectChapterNumber() =
        runTest(testDispatcher) {

            // Arrange
            coEvery {
                repository.resetChapter(2)
            } just Runs

            viewModel = PracticeViewModel(repository, questionRepository)
            advanceUntilIdle()

            // Act
            viewModel.resetChapter(2)
            advanceUntilIdle()

            // Assert
            coVerify(exactly = 1) {
                repository.resetChapter(2)
            }
        }

    // UT-017: Reset chương và tải lại danh sách
    @Test
    fun resetChapter_refreshesChapterListAfterReset() =
        runTest(testDispatcher) {

            // Arrange
            val updatedQuestionTypes = listOf(
                QuestionTypeEntity(2001, "Điểm liệt")
            )

            every {
                repository.getAllQuestionTypes()
            } returnsMany listOf(
                flowOf(emptyList()),
                flowOf(updatedQuestionTypes)
            )

            every {
                questionRepository.getCriticalQuestionCount()
            } returns flowOf(10)

            every {
                repository.getAnsweredCriticalQuestionCount()
            } returns flowOf(2)

            coEvery {
                repository.resetChapter(1)
            } just Runs

            viewModel = PracticeViewModel(repository, questionRepository)
            advanceUntilIdle()

            // Kiểm tra danh sách ban đầu
            assertEquals(0, viewModel.chapters.value.size)

            // Act
            viewModel.resetChapter(1)
            advanceUntilIdle()

            // Assert
            assertEquals(1, viewModel.chapters.value.size)
            assertEquals(
                "Điểm liệt",
                viewModel.chapters.value.first().title
            )

            coVerify(exactly = 1) {
                repository.resetChapter(1)
            }
        }
}