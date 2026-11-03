package com.example.drivertest.ui.practice.practice_question

import PracticeProgressEntity
import UserQuestionProgressEntity
import com.example.drivertest.data.local.entity.AnswerEntity
import com.example.drivertest.data.local.entity.ImageEntity
import com.example.drivertest.data.local.entity.QuestionEntity
import com.example.drivertest.data.repository.PracticeRepository
import com.example.drivertest.data.repository.QuestionRepository
import com.example.drivertest.data.repository.WrongQuestionRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PracticeQuestionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var questionRepository: QuestionRepository
    private lateinit var practiceRepository: PracticeRepository
    private lateinit var wrongQuestionRepository: WrongQuestionRepository
    private lateinit var viewModel: PracticeQuestionViewModel

    private val question1 = QuestionEntity(
        questionID = 101,
        questionTypeID = 2002,
        content = "Câu hỏi thứ nhất",
        explanation = "Giải thích câu 1",
        isCritical = false
    )

    private val question2 = QuestionEntity(
        questionID = 102,
        questionTypeID = 2002,
        content = "Câu hỏi thứ hai",
        explanation = "Giải thích câu 2",
        isCritical = false
    )

    private val answer1 = AnswerEntity(
        answerID = 1,
        questionID = 101,
        content = "Đáp án A",
        isCorrect = true
    )

    private val answer2 = AnswerEntity(
        answerID = 2,
        questionID = 101,
        content = "Đáp án B",
        isCorrect = false
    )

    private val image1 = ImageEntity(
        imageID = 1,
        questionID = 101,
        imageUrl = "image1.jpg",
        imageOrder = 1
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        questionRepository = mockk()
        practiceRepository = mockk()
        wrongQuestionRepository = mockk()

        // Mock QuestionRepository
        every {
            questionRepository.getCriticalQuestions()
        } returns flowOf(emptyList())

        every {
            questionRepository.getQuestionsByType(any())
        } returns flowOf(emptyList())

        coEvery {
            questionRepository.getAnswersByQuestionID(any())
        } returns listOf(answer1, answer2)

        coEvery {
            questionRepository.getImagesByQuestionID(any())
        } returns listOf(image1)

        // Mock PracticeRepository
        coEvery {
            practiceRepository.getPracticeProgress(any())
        } returns null

        coEvery {
            practiceRepository.getQuestionProgress(any(), any())
        } returns null

        coEvery {
            practiceRepository.savePracticeProgress(any())
        } just Runs

        coEvery {
            practiceRepository.saveQuestionProgress(any())
        } just Runs

        // Mock WrongQuestionRepository
        coEvery {
            wrongQuestionRepository.addWrongQuestion(any())
        } just Runs

        viewModel = PracticeQuestionViewModel(
            questionRepository,
            practiceRepository,
            wrongQuestionRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // UT-018: Tải câu hỏi điểm liệt
    @Test
    fun loadQuestion_chapterOne_loadsCriticalQuestions() =
        runTest(testDispatcher) {

            every {
                questionRepository.getCriticalQuestions()
            } returns flowOf(listOf(question1, question2))

            viewModel.loadQuestion(1, 2001)
            advanceUntilIdle()

            assertEquals(101, viewModel.question.value?.question?.questionID)
            assertEquals(2, viewModel.getTotalQuestionCount())
            assertFalse(viewModel.isLoading.value)
        }

    // UT-019: Tải câu hỏi theo chương
    @Test
    fun loadQuestion_otherChapter_loadsQuestionsByType() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(listOf(question1, question2))

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            assertEquals(101, viewModel.question.value?.question?.questionID)
            assertEquals(2, viewModel.getTotalQuestionCount())

            io.mockk.verify {
                questionRepository.getQuestionsByType(2002)
            }
        }

    // UT-020: Danh sách câu hỏi trống
    @Test
    fun loadQuestion_emptyList_questionIsNull() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(emptyList())

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            assertNull(viewModel.question.value)
            assertFalse(viewModel.isLoading.value)
            assertEquals(0, viewModel.getTotalQuestionCount())
        }

    // UT-021: Khôi phục tiến độ
    @Test
    fun loadQuestion_savedProgress_restoresCorrectQuestion() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(listOf(question1, question2))

            coEvery {
                practiceRepository.getPracticeProgress(2)
            } returns PracticeProgressEntity(
                chapterNumber = 2,
                currentQuestionID = 102
            )

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            assertEquals(102, viewModel.question.value?.question?.questionID)
            assertEquals(2, viewModel.getCurrentQuestionNumber())
            assertFalse(viewModel.isFirstQuestion())
        }

    // UT-022: Tiến độ đã lưu không hợp lệ
    @Test
    fun loadQuestion_invalidSavedProgress_startsFromFirstQuestion() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(listOf(question1, question2))

            coEvery {
                practiceRepository.getPracticeProgress(2)
            } returns PracticeProgressEntity(
                chapterNumber = 2,
                currentQuestionID = 999
            )

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            assertEquals(101, viewModel.question.value?.question?.questionID)
            assertEquals(1, viewModel.getCurrentQuestionNumber())
        }

    // UT-023: Chuyển sang câu tiếp theo
    @Test
    fun nextQuestion_movesToNextQuestion() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(listOf(question1, question2))

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            viewModel.nextQuestion()
            advanceUntilIdle()

            assertEquals(102, viewModel.question.value?.question?.questionID)
            assertEquals(2, viewModel.getCurrentQuestionNumber())
            assertTrue(viewModel.isLastQuestion())
        }

    // UT-024: Quay lại câu trước
    @Test
    fun previousQuestion_movesToPreviousQuestion() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(listOf(question1, question2))

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            viewModel.nextQuestion()
            advanceUntilIdle()

            viewModel.previousQuestion()
            advanceUntilIdle()

            assertEquals(101, viewModel.question.value?.question?.questionID)
            assertEquals(1, viewModel.getCurrentQuestionNumber())
            assertTrue(viewModel.isFirstQuestion())
        }

    // UT-025: Không chuyển vượt quá giới hạn
    @Test
    fun navigation_atBoundaries_doesNotMoveOutsideList() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(listOf(question1, question2))

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            // Không thể quay lại trước câu đầu tiên
            viewModel.previousQuestion()
            advanceUntilIdle()

            assertEquals(101, viewModel.question.value?.question?.questionID)

            // Chuyển đến câu cuối cùng
            viewModel.nextQuestion()
            advanceUntilIdle()

            // Không thể chuyển tiếp sau câu cuối cùng
            viewModel.nextQuestion()
            advanceUntilIdle()

            assertEquals(102, viewModel.question.value?.question?.questionID)
            assertTrue(viewModel.isLastQuestion())
        }

    // UT-026: Chọn đáp án đúng
    @Test
    fun selectAnswer_correctAnswer_savesProgressWithoutAddingWrongQuestion() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(listOf(question1))

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            viewModel.selectAnswer(0)
            advanceUntilIdle()

            coVerify {
                practiceRepository.saveQuestionProgress(
                    match {
                        it.chapterNumber == 2 &&
                                it.questionID == 101 &&
                                it.selectedAnswer == "A" &&
                                it.isAnswered &&
                                it.isCorrect == true
                    }
                )
            }

            coVerify(exactly = 0) {
                wrongQuestionRepository.addWrongQuestion(any())
            }
        }

    // UT-027: Chọn đáp án sai
    @Test
    fun selectAnswer_wrongAnswer_savesProgressAndAddsWrongQuestion() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(listOf(question1))

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            viewModel.selectAnswer(1)
            advanceUntilIdle()

            coVerify {
                practiceRepository.saveQuestionProgress(
                    match {
                        it.chapterNumber == 2 &&
                                it.questionID == 101 &&
                                it.selectedAnswer == "B" &&
                                it.isAnswered &&
                                it.isCorrect == false
                    }
                )
            }

            coVerify(exactly = 1) {
                wrongQuestionRepository.addWrongQuestion(101)
            }
        }

    // UT-028: Chọn vị trí đáp án không hợp lệ
    @Test
    fun selectAnswer_invalidPosition_doesNotSaveProgress() =
        runTest(testDispatcher) {

            every {
                questionRepository.getQuestionsByType(2002)
            } returns flowOf(listOf(question1))

            viewModel.loadQuestion(2, 2002)
            advanceUntilIdle()

            viewModel.selectAnswer(-1)
            viewModel.selectAnswer(10)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                practiceRepository.saveQuestionProgress(any())
            }

            coVerify(exactly = 0) {
                wrongQuestionRepository.addWrongQuestion(any())
            }
        }
}