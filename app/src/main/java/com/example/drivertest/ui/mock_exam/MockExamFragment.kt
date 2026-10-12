package com.example.drivertest.ui.mock_exam

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivertest.R
import com.example.drivertest.databinding.FragmentMockExamBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MockExamFragment : Fragment(R.layout.fragment_mock_exam) {

    private var _binding: FragmentMockExamBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MockExamViewModel by viewModels()

    private lateinit var mockExamAdapter: MockExamAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMockExamBinding.bind(view)

        setupRecyclerView()
        setupListeners()
        observeUiState()

        viewModel.loadExams()
    }

    private fun setupRecyclerView() {
        mockExamAdapter = MockExamAdapter { exam ->
            val bundle = Bundle().apply {
                putInt("mockExamID", exam.mockExamID)
                putInt("examNumber", exam.examNumber)
            }

            findNavController().navigate(
                R.id.action_mockExamFragment_to_mockExamTakingFragment,
                bundle
            )
        }

        binding.rvMockExams.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mockExamAdapter
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MockExamListUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvMockExams.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                        }

                        is MockExamListUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvMockExams.visibility = View.VISIBLE
                            binding.tvError.visibility = View.GONE

                            mockExamAdapter.submitList(state.exams)
                        }

                        is MockExamListUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvMockExams.visibility = View.GONE
                            binding.tvError.visibility = View.VISIBLE
                            binding.tvError.text = state.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}