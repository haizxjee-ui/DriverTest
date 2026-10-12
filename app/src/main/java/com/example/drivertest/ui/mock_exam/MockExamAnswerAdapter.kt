package com.example.drivertest.ui.mock_exam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drivertest.R
import com.example.drivertest.data.local.entity.AnswerEntity
import com.example.drivertest.databinding.ItemPracticeAnswerBinding

class MockExamAnswerAdapter(
    private val onAnswerClick: (AnswerEntity) -> Unit
) : RecyclerView.Adapter<MockExamAnswerAdapter.AnswerViewHolder>() {

    private val answers = mutableListOf<AnswerEntity>()
    private var selectedAnswerID: Int? = null

    fun submitAnswers(
        newList: List<AnswerEntity>,
        selectedID: Int?
    ) {
        val answersChanged = answers != newList
        val selectionChanged = selectedAnswerID != selectedID

        if (!answersChanged && !selectionChanged) return

        answers.clear()
        answers.addAll(newList)
        selectedAnswerID = selectedID

        notifyDataSetChanged()
    }

    fun updateSelectedAnswer(answerID: Int) {
        if (selectedAnswerID == answerID) return

        val oldPosition = answers.indexOfFirst {
            it.answerID == selectedAnswerID
        }

        val newPosition = answers.indexOfFirst {
            it.answerID == answerID
        }

        selectedAnswerID = answerID

        if (oldPosition != -1) notifyItemChanged(oldPosition)
        if (newPosition != -1) notifyItemChanged(newPosition)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnswerViewHolder {
        val binding = ItemPracticeAnswerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AnswerViewHolder,
        position: Int
    ) {
        holder.bind(answers[position])
    }

    override fun getItemCount(): Int = answers.size

    inner class AnswerViewHolder(
        private val binding: ItemPracticeAnswerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(answer: AnswerEntity) {
            val position = bindingAdapterPosition
            val labels = listOf("A", "B", "C", "D", "E")

            binding.tvAnswerLabel.text =
                labels.getOrElse(position) { "" }

            binding.tvAnswerContent.text = answer.content

            val isSelected = answer.answerID == selectedAnswerID

            binding.root.setBackgroundResource(
                if (isSelected) {
                    R.drawable.bg_mock_exam_answer_selected
                } else {
                    R.drawable.bg_practice_answer
                }
            )

            binding.root.isClickable = true
            binding.root.setOnClickListener {
                onAnswerClick(answer)
            }
        }
    }
}