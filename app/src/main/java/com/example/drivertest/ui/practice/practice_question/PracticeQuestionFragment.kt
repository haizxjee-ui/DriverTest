package com.example.drivertest.ui.practice.practice_question

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
import coil.load
import com.example.drivertest.R
import com.example.drivertest.data.local.entity.AnswerEntity
import com.example.drivertest.data.local.entity.ImageEntity
import com.example.drivertest.databinding.FragmentPracticeQuestionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PracticeQuestionFragment :
    Fragment(R.layout.fragment_practice_question) {

    private var _binding: FragmentPracticeQuestionBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            PracticeQuestionViewModel by viewModels()

    private val chapterNumber: Int
        get() = requireArguments()
            .getInt("chapterNumber")

    private val questionTypeID: Int
        get() = requireArguments()
            .getInt("questionTypeID")

    private lateinit var answerAdapter: AnswerAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        _binding =
            FragmentPracticeQuestionBinding.bind(
                view
            )

        setupBackButton()
        setupAnswerRecyclerView()
        setupNavigationButtons()
        observeQuestion()

        viewModel.loadQuestion(
            chapterNumber = chapterNumber,
            questionTypeID = questionTypeID
        )
    }

    private fun setupBackButton() {

        binding.btnBack.setOnClickListener {

            findNavController().navigateUp()
        }
    }

    private fun setupAnswerRecyclerView() {

        answerAdapter =
            AnswerAdapter { answer, position ->

                handleAnswerSelected(
                    answer = answer,
                    position = position
                )
            }

        binding.rvAnswers.apply {

            layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(
                    requireContext()
                )

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

    private fun observeQuestion() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.question.collect { question ->

                    if (question != null) {

                        displayQuestion(
                            question
                        )

                        updateNavigationButtons()
                    }
                }
            }
        }
    }

    private fun displayQuestion(
        question: PracticeQuestionUiModel
    ) {
        binding.tvQuestion.text =
            question.question.content

        binding.tvQuestionNumber.text =
            "Câu ${viewModel.getCurrentQuestionNumber()} / ${viewModel.getTotalQuestionCount()}"

        if (question.question.isCritical) {
            binding.tvCritical.visibility = View.VISIBLE
        } else {
            binding.tvCritical.visibility = View.GONE
        }

        answerAdapter.submitList(question.answers)

        displayImages(question.images)

        val userProgress = question.userProgress

        if (
            userProgress != null &&
            userProgress.isAnswered &&
            userProgress.selectedAnswer != null
        ) {
            answerAdapter.restoreResult(
                userProgress.selectedAnswer
            )
            showExplanation()
        } else {
            binding.layoutExplanation.visibility = View.GONE
        }
    }

    private fun updateNavigationButtons() {

        binding.btnPrevious.isEnabled =
            !viewModel.isFirstQuestion()

        binding.btnNext.isEnabled =
            !viewModel.isLastQuestion()
    }

    private fun displayImages(
        images: List<ImageEntity>
    ) {

        binding.layoutImages.removeAllViews()

        if (images.isEmpty()) {

            binding.layoutImages.visibility =
                View.GONE

            return
        }

        binding.layoutImages.visibility =
            View.VISIBLE

        images.forEach { image ->

            val imageView =
                ImageView(requireContext())

            imageView.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {

                    bottomMargin =
                        (
                                12 *
                                        resources
                                            .displayMetrics
                                            .density
                                ).toInt()
                }

            imageView.adjustViewBounds =
                true

            imageView.scaleType =
                ImageView.ScaleType.FIT_CENTER

            imageView.load(
                image.imageUrl
            ) {

                crossfade(true)
            }

            binding.layoutImages.addView(
                imageView
            )
        }
    }

    private fun handleAnswerSelected(
        answer: AnswerEntity,
        position: Int
    ) {

        answerAdapter.showResult(
            position
        )

        showExplanation()

        viewModel.selectAnswer(
            position
        )
    }

    private fun showExplanation() {

        binding.layoutExplanation.visibility =
            View.VISIBLE

        val explanation =
            viewModel.question.value
                ?.question
                ?.explanation

        binding.tvExplanation.text =
            explanation
                ?: "Không có giải thích cho câu hỏi này."
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}