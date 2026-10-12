package com.example.drivertest.ui.mock_exam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drivertest.data.local.entity.MockExamEntity
import com.example.drivertest.databinding.ItemMockExamBinding

class MockExamAdapter(
    private val onExamClick: (MockExamEntity) -> Unit
) : RecyclerView.Adapter<MockExamAdapter.MockExamViewHolder>() {

    private val exams = mutableListOf<MockExamEntity>()

    inner class MockExamViewHolder(
        private val binding: ItemMockExamBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(exam: MockExamEntity) {
            val number = exam.examNumber

            binding.tvExamNumber.text = number.toString().padStart(2, '0')
            binding.tvExamTitle.text = "Đề thi số ${number.toString().padStart(2, '0')}"
            binding.tvExamQuestionCount.text = "Nhấn để bắt đầu làm bài"

            binding.root.setOnClickListener {
                onExamClick(exam)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MockExamViewHolder {
        val binding = ItemMockExamBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MockExamViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MockExamViewHolder,
        position: Int
    ) {
        holder.bind(exams[position])
    }

    override fun getItemCount(): Int = exams.size

    fun submitList(newExams: List<MockExamEntity>) {
        exams.clear()
        exams.addAll(newExams)
        notifyDataSetChanged()
    }
}