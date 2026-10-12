package com.example.drivertest.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drivertest.R
import com.example.drivertest.data.local.entity.ExamEntity
import java.util.Locale

class ExamResultAdapter(
    private val onExamClick: (ExamEntity) -> Unit
) : RecyclerView.Adapter<ExamResultAdapter.ExamResultViewHolder>() {

    private var items: List<ExamEntity> = emptyList()

    fun submitList(newItems: List<ExamEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exam_result, parent, false)
        return ExamResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExamResultViewHolder, position: Int) {
        holder.bind(items[position], position + 1, onExamClick)
    }

    override fun getItemCount(): Int = items.size

    class ExamResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvExamName: TextView = itemView.findViewById(R.id.tvExamName)
        private val tvResultStatus: TextView = itemView.findViewById(R.id.tvResultStatus)
        private val tvResultDetail: TextView = itemView.findViewById(R.id.tvResultDetail)
        private val ivResultIcon: ImageView = itemView.findViewById(R.id.ivResultIcon)

        fun bind(exam: ExamEntity, displayIndex: Int, onExamClick: (ExamEntity) -> Unit) {
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
                onExamClick(exam)
            }
        }
    }
}
