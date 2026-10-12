package com.example.drivertest.ui.mock_exam

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivertest.R
import com.example.drivertest.databinding.FragmentExamResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExamResultFragment : Fragment(R.layout.fragment_exam_result) {

    private var _binding: FragmentExamResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExamResultViewModel by viewModels()

    private lateinit var resultAdapter: ExamResultQuestionAdapter

    private val examID: Int
        get() = requireArguments().getInt("examID")

    private val examNumber: Int
        get() = requireArguments().getInt("examNumber", 1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentExamResultBinding.bind(view)

        setupRecyclerView()
        setupListeners()
        observeUiState()

        viewModel.loadResult(examID)
    }

    private fun setupRecyclerView() {
        resultAdapter = ExamResultQuestionAdapter()

        binding.rvResultQuestions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = resultAdapter
            setHasFixedSize(false)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            // Quay về danh sách đề thi, tránh quay lại màn hình đang làm bài.
            val popped = findNavController()
                .popBackStack(R.id.mockExamFragment, false)

            if (!popped) {
                findNavController().navigateUp()
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility =
                        if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.isLoading) {
                        binding.rvResultQuestions.visibility = View.GONE
                        binding.tvError.visibility = View.GONE
                        return@collect
                    }

                    val errorMessage = state.errorMessage
                    if (errorMessage != null) {
                        binding.rvResultQuestions.visibility = View.GONE
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = errorMessage
                        return@collect
                    }

                    val exam = state.exam ?: return@collect

                    binding.tvError.visibility = View.GONE
                    binding.rvResultQuestions.visibility = View.VISIBLE

                    resultAdapter.setData(exam, examNumber, state.questions)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
