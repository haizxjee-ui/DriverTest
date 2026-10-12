package com.example.drivertest.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.drivertest.R
import com.example.drivertest.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        setupClickListeners()
        observeCurrentLicense()
        observeWrongQuestionCount()
    }

    private fun setupClickListeners() {

        binding.cardChangeLicense.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_licenseSelectionFragment
            )
        }

        binding.cardPractice.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_practiceFragment
            )
        }

        binding.cardWrongQuestions.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_wrongQuestionFragment
            )
        }

        binding.cardMockExam.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_mockExamFragment
            )
        }
    }

    private fun observeCurrentLicense() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.currentLicense.collect { licenseType ->

                    licenseType?.let {
                        binding.tvCurrentLicense.text =
                            "Hạng ${it.name}"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeWrongQuestionCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.wrongQuestionCount.collect { count ->
                    binding.tvWrongQuestionCount.text = "$count câu"
                }
            }
        }
    }
}