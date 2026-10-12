package com.example.drivertest.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drivertest.R
import com.example.drivertest.data.local.entity.ExamEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExamHistoryAdapter(
    private val onExamClick: (ExamEntity) -> Unit
) : RecyclerView.Adapter<ExamHistoryAdapter.ExamHistoryViewHolder>() {

    private var items: List<ExamEntity> = emptyList()

    fun submitList(newItems: List<ExamEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exam_history, parent, false)
        return ExamHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExamHistoryViewHolder, position: Int) {
        holder.bind(items[position], onExamClick)
    }

    override fun getItemCount(): Int = items.size

    class ExamHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvExamTitle: TextView = itemView.findViewById(R.id.tvExamTitle)
        private val tvExamDate: TextView = itemView.findViewById(R.id.tvExamDate)
        private val tvExamStatus: TextView = itemView.findViewById(R.id.tvExamStatus)
        private val tvExamScore: TextView = itemView.findViewById(R.id.tvExamScore)

        fun bind(exam: ExamEntity, onExamClick: (ExamEntity) -> Unit) {
            tvExamTitle.text = "Lần thi #${exam.examID}"
            tvExamScore.text = "${exam.score} câu đúng"

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvExamDate.text = sdf.format(Date(exam.startTime))

            if (exam.result) {
                tvExamStatus.text = "ĐẠT"
                tvExamStatus.setTextColor(Color.rgb(21, 128, 61))
                tvExamStatus.setBackgroundColor(Color.rgb(220, 252, 231))
            } else {
                tvExamStatus.text = "KHÔNG ĐẠT"
                tvExamStatus.setTextColor(Color.rgb(185, 28, 28))
                tvExamStatus.setBackgroundColor(Color.rgb(254, 226, 226))
            }

            itemView.setOnClickListener {
                onExamClick(exam)
            }
        }
    }
}
