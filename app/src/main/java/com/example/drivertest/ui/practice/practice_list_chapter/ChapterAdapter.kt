package com.example.drivertest.ui.practice.practice_list_chapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drivertest.databinding.ItemChapterBinding

class ChapterAdapter(
    private val onChapterClick: (ChapterUiModel) -> Unit,
    private val onResetClick: (ChapterUiModel) -> Unit
) : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    private val chapters = mutableListOf<ChapterUiModel>()


    fun submitList(newList: List<ChapterUiModel>) {

        // Bỏ chương 9 - bộ đề thi thử lý thuyết
        val practiceChapters = newList.filter {
            it.chapterNumber != 9
        }

        chapters.clear()
        chapters.addAll(practiceChapters)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChapterViewHolder {

        val binding = ItemChapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ChapterViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ChapterViewHolder,
        position: Int
    ) {
        holder.bind(chapters[position])
    }

    override fun getItemCount(): Int {
        return chapters.size
    }

    inner class ChapterViewHolder(
        private val binding: ItemChapterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chapter: ChapterUiModel) {

            binding.tvChapterTag.text =
                "Chương ${chapter.chapterNumber}"

            binding.tvChapterTitle.text =
                chapter.title

            binding.tvProgressCount.text =
                "Tiến độ: ${chapter.answeredQuestions}/${chapter.totalQuestions} câu"

            binding.tvProgressPercent.text =
                "${chapter.progressPercent}%"

            binding.progressBar.progress =
                chapter.progressPercent

            binding.tvTotalQuestions.text =
                "${chapter.totalQuestions} câu hỏi"

            if (chapter.criticalQuestionCount > 0) {

                binding.tvWarningTag.visibility =
                    View.VISIBLE

                binding.tvWarningTag.text =
                    "${chapter.criticalQuestionCount} câu điểm liệt"

            } else {

                binding.tvWarningTag.visibility =
                    View.GONE
            }

            when {

                chapter.progressPercent == 0 -> {

                    binding.btnContinue.text = "Bắt đầu"

                    binding.btnContinue.setTextColor(
                        android.graphics.Color.parseColor("#1E3A8A")
                    )

                    binding.btnReset.visibility = View.GONE

                    binding.btnContinue.setOnClickListener {
                        onChapterClick(chapter)
                    }
                }

                chapter.progressPercent < 100 -> {

                    binding.btnContinue.text = "Làm tiếp"

                    binding.btnContinue.setTextColor(
                        android.graphics.Color.parseColor("#1E3A8A")
                    )

                    binding.btnReset.visibility = View.VISIBLE

                    binding.btnContinue.setOnClickListener {
                        onChapterClick(chapter)
                    }

                    binding.btnReset.setOnClickListener {
                        onResetClick(chapter)
                    }
                }

                else -> {

                    binding.btnContinue.text = "Làm lại"

                    binding.btnContinue.setTextColor(
                        android.graphics.Color.parseColor("#DC2626")
                    )

                    binding.btnReset.visibility = View.GONE

                    binding.btnContinue.setOnClickListener {
                        onResetClick(chapter)
                    }
                }
            }

            // Click vào card
            binding.root.setOnClickListener {
                onChapterClick(chapter)
            }
        }
    }
}