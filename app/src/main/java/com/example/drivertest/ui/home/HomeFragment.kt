package com.example.drivertest.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.drivertest.R
import com.example.drivertest.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

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
        observeExamHistory()
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

        binding.tvClearHistory.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Xóa lịch sử thi")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ lịch sử các bài thi đã làm không?")
                .setPositiveButton("Xóa") { _, _ ->
                    viewModel.clearExamHistory()
                }
                .setNegativeButton("Hủy", null)
                .show()
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

    private fun observeWrongQuestionCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.wrongQuestionCount.collect { count ->
                    binding.tvWrongQuestionCount.text = "$count câu"
                }
            }
        }
    }

    private fun observeExamHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.examHistory.collect { exams ->
                    binding.layoutExamResultsContainer.removeAllViews()

                    exams.forEachIndexed { index, exam ->
                        val itemView = layoutInflater.inflate(
                            R.layout.item_exam_result,
                            binding.layoutExamResultsContainer,
                            false
                        )

                        val tvExamName = itemView.findViewById<TextView>(R.id.tvExamName)
                        val tvResultStatus = itemView.findViewById<TextView>(R.id.tvResultStatus)
                        val tvResultDetail = itemView.findViewById<TextView>(R.id.tvResultDetail)
                        val ivResultIcon = itemView.findViewById<ImageView>(R.id.ivResultIcon)

                        val displayIndex = index + 1
                        tvExamName.text = "Bài thi #$displayIndex"

                        val durationSeconds = ((exam.endTime - exam.startTime).coerceAtLeast(0L) / 1000).toInt()
                        val minutes = durationSeconds / 60
                        val seconds = durationSeconds % 60
                        val timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                        tvResultDetail.text = "${exam.score} câu đúng   ⏱ $timeStr"

                        if (exam.result) {
                            tvResultStatus.text = "ĐẠT"
                            tvResultStatus.setTextColor(Color.parseColor("#059669"))
                            tvResultStatus.setBackgroundColor(Color.parseColor("#DCFCE7"))
                            ivResultIcon.setImageResource(R.drawable.ic_check_circle)
                            ivResultIcon.setColorFilter(Color.parseColor("#059669"))
                        } else {
                            tvResultStatus.text = "KHÔNG ĐẠT"
                            tvResultStatus.setTextColor(Color.parseColor("#DC2626"))
                            tvResultStatus.setBackgroundColor(Color.parseColor("#FEE2E2"))
                            ivResultIcon.setImageResource(R.drawable.ic_assignment)
                            ivResultIcon.setColorFilter(Color.parseColor("#DC2626"))
                        }

                        itemView.setOnClickListener {
                            val bundle = Bundle().apply {
                                putInt("examID", exam.examID)
                                putInt("examNumber", displayIndex)
                            }
                            findNavController().navigate(
                                R.id.action_homeFragment_to_examResultFragment,
                                bundle
                            )
                        }

                        binding.layoutExamResultsContainer.addView(itemView)
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
