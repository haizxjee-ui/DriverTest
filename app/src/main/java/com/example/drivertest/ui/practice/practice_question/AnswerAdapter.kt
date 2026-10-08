package com.example.drivertest.ui.practice.practice_question

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drivertest.data.local.entity.AnswerEntity
import com.example.drivertest.databinding.ItemPracticeAnswerBinding

class AnswerAdapter(
    private val onAnswerClick: (AnswerEntity, Int) -> Unit
) : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    private val answers = mutableListOf<AnswerEntity>()

    private var selectedPosition = -1
    private var answerSubmitted = false

    fun submitList(newList: List<AnswerEntity>) {

        answers.clear()
        answers.addAll(newList)

        selectedPosition = -1
        answerSubmitted = false

        notifyDataSetChanged()
    }

    fun showResult(position: Int) {

        selectedPosition = position
        answerSubmitted = true

        notifyDataSetChanged()
    }

    fun restoreResult(
        selectedAnswer: String
    ) {

        val answerLabels = listOf(
            "A",
            "B",
            "C",
            "D",
            "E"
        )

        val position =
            answerLabels.indexOf(selectedAnswer)

        if (position == -1) {
            return
        }

        selectedPosition = position
        answerSubmitted = true

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnswerViewHolder {

        val binding =
            ItemPracticeAnswerBinding.inflate(
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

        holder.bind(
            answer = answers[position],
            position = position
        )
    }

    override fun getItemCount(): Int =
        answers.size

    inner class AnswerViewHolder(
        private val binding: ItemPracticeAnswerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            answer: AnswerEntity,
            position: Int
        ) {

            val answerLabels = listOf(
                "A",
                "B",
                "C",
                "D",
                "E"
            )

            binding.tvAnswerLabel.text =
                answerLabels[position]

            binding.tvAnswerContent.text =
                answer.content

            if (answerSubmitted) {

                binding.root.isClickable = false

                if (answer.isCorrect) {

                    binding.root.setBackgroundResource(
                        com.example.drivertest.R.drawable.bg_answer_correct
                    )

                } else if (position == selectedPosition) {

                    binding.root.setBackgroundResource(
                        com.example.drivertest.R.drawable.bg_answer_wrong
                    )

                } else {

                    binding.root.setBackgroundResource(
                        com.example.drivertest.R.drawable.bg_practice_answer
                    )
                }

            } else {

                binding.root.isClickable = true

                binding.root.setBackgroundResource(
                    com.example.drivertest.R.drawable.bg_practice_answer
                )

                binding.root.setOnClickListener {

                    onAnswerClick(
                        answer,
                        position
                    )
                }
            }
        }
    }
}