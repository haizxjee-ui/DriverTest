package com.example.drivertest.ui.practice.practice_wrong_question

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
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
import com.example.drivertest.data.model.PracticeQuestion
import com.example.drivertest.databinding.FragmentWrongQuestionBinding
import com.example.drivertest.ui.practice.practice_question.AnswerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WrongQuestionFragment :
    Fragment(R.layout.fragment_wrong_question) {

    private var _binding: FragmentWrongQuestionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WrongQuestionViewModel by viewModels()

    private lateinit var answerAdapter: AnswerAdapter

    private var isLeaving = false

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentWrongQuestionBinding.bind(view)

        setupAnswerRecyclerView()
        setupNavigationButtons()
        setupBackButton()
        observeQuestions()

        viewModel.loadQuestions()
    }

    private fun setupAnswerRecyclerView() {
        answerAdapter = AnswerAdapter { _, position ->
            handleAnswerSelected(position)
        }

        binding.rvAnswers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = answerAdapter
            setHasFixedSize(false)
        }
    }

    private fun setupNavigationButtons() {
        binding.btnPrevious.setOnClickListener {
            viewModel.previousQuestion()
        }

        binding.btnNext.setOnClickListener {
            viewModel.nextQuestion()
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            leaveScreen()
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        leaveScreen()
                    }
                }
            )
    }

    private fun leaveScreen() {
        if (isLeaving) return

        isLeaving = true
        binding.btnBack.isEnabled = false

        viewModel.removeAnsweredQuestions {
            if (!isAdded) return@removeAnsweredQuestions

            val navController = findNavController()

            if (!navController.popBackStack(R.id.homeFragment, false)) {
                navController.navigate(R.id.homeFragment)
            }
        }
    }

    private fun observeQuestions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                combine(
                    viewModel.questions,
                    viewModel.currentIndex,
                    viewModel.answerStates
                ) { questions, index, answerStates ->
                    Triple(questions, index, answerStates)
                }.collect { (questions, index, answerStates) ->

                    if (questions.isEmpty()) {
                        showEmptyState()
                        return@collect
                    }

                    val question = questions.getOrNull(index)
                        ?: return@collect

                    displayQuestion(
                        question = question,
                        questionNumber = index + 1,
                        totalQuestions = questions.size
                    )

                    val questionID = question.question.questionID
                    val answerState = answerStates[questionID]

                    if (answerState != null) {
                        val answerLabels = listOf("A", "B", "C", "D", "E")
                        val selectedPosition =
                            answerLabels.indexOf(answerState.selectedAnswer)

                        if (selectedPosition >= 0) {
                            answerAdapter.restoreResult(
                                answerState.selectedAnswer
                            )
                        }

                        showExplanation(question)
                    } else {
                        binding.layoutExplanation.visibility = View.GONE
                    }

                    updateNavigationButtons(
                        index = index,
                        totalQuestions = questions.size
                    )
                }
            }
        }
    }

    private fun displayQuestion(
        question: PracticeQuestion,
        questionNumber: Int,
        totalQuestions: Int
    ) {
        binding.tvQuestionNumber.text =
            "Câu $questionNumber / $totalQuestions"

        binding.tvQuestion.text = question.question.content

        binding.tvCritical.visibility =
            if (question.question.isCritical) {
                View.VISIBLE
            } else {
                View.GONE
            }

        answerAdapter.submitList(question.answers)

        displayImages(question.images)

        binding.layoutExplanation.visibility = View.GONE
        binding.rvAnswers.visibility = View.VISIBLE
    }

    private fun handleAnswerSelected(position: Int) {
        val question = viewModel.getCurrentQuestion()
            ?: return

        val questionID = question.question.questionID

        // Nếu câu đã được trả lời, không xử lý thêm.
        if (viewModel.getAnswerState(questionID) != null) return

        viewModel.selectAnswer(position)

        // Cập nhật giao diện ngay sau khi chọn đáp án.
        answerAdapter.showResult(position)
        showExplanation(question)
    }

    private fun showExplanation(question: PracticeQuestion) {
        binding.layoutExplanation.visibility = View.VISIBLE

        binding.tvExplanation.text =
            question.question.explanation
                ?: "Không có giải thích cho câu hỏi này."
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

            imageView.layoutParams =
                LinearLayout.LayoutParams(
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

    private fun updateNavigationButtons(
        index: Int,
        totalQuestions: Int
    ) {
        binding.btnPrevious.isEnabled = index > 0
        binding.btnNext.isEnabled = index < totalQuestions - 1
    }

    private fun showEmptyState() {
        binding.tvQuestionNumber.text = "0 / 0"
        binding.tvQuestion.text =
            "Bạn chưa có câu hỏi sai nào cần ôn tập."

        binding.tvCritical.visibility = View.GONE
        binding.layoutImages.removeAllViews()
        binding.layoutImages.visibility = View.GONE
        binding.rvAnswers.visibility = View.GONE
        binding.layoutExplanation.visibility = View.GONE

        binding.btnPrevious.isEnabled = false
        binding.btnNext.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}