package com.example.drivertest.ui.mock_exam

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.drivertest.R
import com.example.drivertest.data.local.entity.ImageEntity
import com.example.drivertest.databinding.FragmentMockExamTakingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MockExamTakingFragment :
    Fragment(R.layout.fragment_mock_exam_taking) {

    private var _binding: FragmentMockExamTakingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MockExamTakingViewModel by viewModels()

    private lateinit var answerAdapter: MockExamAnswerAdapter

    // ID câu hỏi đang hiển thị.
    // Chỉ vẽ lại nội dung khi chuyển sang câu hỏi khác.
    private var displayedQuestionID: Int? = null

    // Đáp án đang hiển thị để tránh cập nhật Adapter không cần thiết.
    private var displayedSelectedAnswerID: Int? = null

    private val mockExamID: Int
        get() = requireArguments().getInt("mockExamID")

    private val examNumber: Int
        get() = requireArguments().getInt("examNumber", 1)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMockExamTakingBinding.bind(view)

        setupAnswerRecyclerView()
        setupNavigationButtons()
        observeUiState()
        startTimer()

        viewModel.loadExam(mockExamID)
    }

    private fun setupAnswerRecyclerView() {
        answerAdapter = MockExamAnswerAdapter { answer ->
            val state = viewModel.uiState.value

            if (state.isSubmitted || state.questions.isEmpty()) {
                return@MockExamAnswerAdapter
            }

            val currentQuestion = state.questions.getOrNull(
                state.currentQuestionIndex
            ) ?: return@MockExamAnswerAdapter

            viewModel.selectAnswer(
                questionID = currentQuestion.question.questionID,
                answerID = answer.answerID
            )
        }

        binding.rvAnswers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = answerAdapter
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
        }
    }

    private fun setupNavigationButtons() {
        binding.btnPrevious.setOnClickListener {
            viewModel.previousQuestion()
        }

        binding.btnNext.setOnClickListener {
            viewModel.nextQuestion()
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.submitExam()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.progressBar.visibility =
                        if (state.isLoading) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                    if (state.isLoading) {
                        return@collect
                    }

                    val errorMessage = state.errorMessage

                    if (errorMessage != null) {
                        binding.tvQuestion.text = errorMessage
                        return@collect
                    }

                    if (state.questions.isEmpty()) {
                        binding.tvQuestion.text = "Đề thi không có câu hỏi."
                        return@collect
                    }

                    val currentQuestion = state.questions.getOrNull(
                        state.currentQuestionIndex
                    ) ?: return@collect

                    val currentQuestionID =
                        currentQuestion.question.questionID

                    val selectedAnswerID =
                        state.selectedAnswers[currentQuestionID]

                    // Chỉ hiển thị lại nội dung khi đổi câu hỏi.
                    if (displayedQuestionID != currentQuestionID) {
                        displayCurrentQuestion(state)

                        displayedQuestionID = currentQuestionID
                        displayedSelectedAnswerID = selectedAnswerID
                    } else if (
                        displayedSelectedAnswerID != selectedAnswerID
                    ) {
                        // Chỉ cập nhật danh sách đáp án khi người dùng
                        // thực sự thay đổi lựa chọn.
                        answerAdapter.submitAnswers(
                            newList = currentQuestion.answers,
                            selectedID = selectedAnswerID
                        )

                        displayedSelectedAnswerID = selectedAnswerID
                    }

                    // Đồng hồ cập nhật độc lập, không tải lại câu hỏi.
                    updateTimer(state.remainingTimeSeconds)

                    updateNavigationButtons(state)

                    binding.btnSubmit.isEnabled = !state.isSubmitted

                    if (state.isSubmitted) {
                        showExamResult(
                            correctCount = state.correctCount,
                            totalQuestions = state.questions.size,
                            isPassed = state.isPassed
                        )
                    }
                }
            }
        }
    }

    private fun displayCurrentQuestion(
        state: MockExamTakingUiState
    ) {
        val item = state.questions.getOrNull(
            state.currentQuestionIndex
        ) ?: return

        binding.tvTitle.text = "Đề thi số $examNumber"

        binding.tvQuestionNumber.text =
            "Câu ${state.currentQuestionIndex + 1} / ${state.questions.size}"

        binding.tvQuestion.text = item.question.content

        binding.tvCritical.visibility =
            if (item.question.isCritical) {
                View.VISIBLE
            } else {
                View.GONE
            }

        displayImages(item.images)

        val selectedAnswerID =
            state.selectedAnswers[item.question.questionID]

        answerAdapter.submitAnswers(
            newList = item.answers,
            selectedID = selectedAnswerID
        )

        binding.rvAnswers.isEnabled = !state.isSubmitted

        updateNavigationButtons(state)
    }

    private fun displayImages(images: List<ImageEntity>) {
        binding.layoutImages.removeAllViews()

        if (images.isEmpty()) {
            binding.layoutImages.visibility = View.GONE
            return
        }

        binding.layoutImages.visibility = View.VISIBLE

        images.forEach { image ->
            val imageView = ImageView(requireContext())

            imageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin =
                    (12 * resources.displayMetrics.density).toInt()
            }

            imageView.adjustViewBounds = true
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER

            imageView.load(image.imageUrl) {
                crossfade(true)
            }

            binding.layoutImages.addView(imageView)
        }
    }

    private fun updateTimer(seconds: Int) {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        binding.tvTimer.text = String.format(
            java.util.Locale.getDefault(),
            "%02d:%02d",
            minutes,
            remainingSeconds
        )
    }

    private fun updateNavigationButtons(
        state: MockExamTakingUiState
    ) {
        binding.btnPrevious.isEnabled =
            !state.isSubmitted &&
                    state.currentQuestionIndex > 0

        binding.btnNext.isEnabled =
            !state.isSubmitted &&
                    state.currentQuestionIndex < state.questions.lastIndex
    }

    private fun startTimer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(1000)
                    viewModel.tickTimer()
                }
            }
        }
    }

    private fun showExamResult(
        correctCount: Int,
        totalQuestions: Int,
        isPassed: Boolean
    ) {
        binding.tvQuestion.text = buildString {
            appendLine(
                if (isPassed) {
                    "KẾT QUẢ: ĐẠT"
                } else {
                    "KẾT QUẢ: KHÔNG ĐẠT"
                }
            )

            append("Số câu đúng: $correctCount/$totalQuestions")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
        displayedQuestionID = null
        displayedSelectedAnswerID = null
    }
}