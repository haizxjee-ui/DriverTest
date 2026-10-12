package com.example.drivertest.ui.mock_exam

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.drivertest.R
import com.example.drivertest.data.local.entity.ExamEntity
import com.example.drivertest.data.local.entity.ImageEntity
import com.example.drivertest.data.model.ExamResultQuestionData

class ExamResultQuestionAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_QUESTION = 1
    }

    private var exam: ExamEntity? = null
    private var examNumber: Int = 1
    private var questions: List<ExamResultQuestionData> = emptyList()

    fun setData(
        newExam: ExamEntity?,
        newExamNumber: Int,
        newQuestions: List<ExamResultQuestionData>
    ) {
        exam = newExam
        examNumber = newExamNumber
        questions = newQuestions
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (questions.isEmpty()) 0 else questions.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_QUESTION
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_exam_result_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_exam_result_question, parent, false)
            ResultViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            (holder as HeaderViewHolder).bind(exam, examNumber, questions.size)
        } else {
            val questionItem = questions[position - 1]
            (holder as ResultViewHolder).bind(questionItem, position - 1)
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvExamTitle: TextView = itemView.findViewById(R.id.tvExamTitle)
        private val tvResultStatus: TextView = itemView.findViewById(R.id.tvResultStatus)
        private val tvCorrectCount: TextView = itemView.findViewById(R.id.tvCorrectCount)
        private val tvExamDuration: TextView = itemView.findViewById(R.id.tvExamDuration)

        fun bind(exam: ExamEntity?, examNumber: Int, totalQuestions: Int) {
            if (exam == null) return

            tvExamTitle.text = "Đề thi số $examNumber"

            tvResultStatus.apply {
                text = if (exam.result) "ĐẠT" else "KHÔNG ĐẠT"
                setTextColor(
                    if (exam.result) {
                        Color.rgb(22, 163, 74)
                    } else {
                        Color.rgb(220, 38, 38)
                    }
                )
            }

            tvCorrectCount.text = "${exam.score} / $totalQuestions câu đúng"

            val durationSeconds =
                ((exam.endTime - exam.startTime).coerceAtLeast(0L) / 1000).toInt()

            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60

            tvExamDuration.text =
                "Thời gian làm bài: %02d phút %02d giây".format(
                    minutes,
                    seconds
                )
        }
    }

    class ResultViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvQuestionNumber: TextView =
            itemView.findViewById(R.id.tvQuestionNumber)

        private val tvAnswerStatus: TextView =
            itemView.findViewById(R.id.tvAnswerStatus)

        private val tvQuestionContent: TextView =
            itemView.findViewById(R.id.tvQuestionContent)

        private val layoutImages: LinearLayout =
            itemView.findViewById(R.id.layoutImages)

        private val layoutAnswers: LinearLayout =
            itemView.findViewById(R.id.layoutAnswers)

        private val layoutExplanation: LinearLayout =
            itemView.findViewById(R.id.layoutExplanation)

        private val tvExplanation: TextView =
            itemView.findViewById(R.id.tvExplanation)

        fun bind(
            item: ExamResultQuestionData,
            position: Int
        ) {
            val detail = item.detail
            val question = item.question
            val answers = item.answers

            displayImages(item.images)

            tvQuestionNumber.text = "Câu ${position + 1}"
            tvQuestionContent.text = question.content

            when {
                detail.selectedAnswerID == null -> {
                    tvAnswerStatus.text = "Chưa trả lời"
                    tvAnswerStatus.setTextColor(Color.rgb(180, 83, 9))
                    tvAnswerStatus.setBackgroundColor(Color.rgb(254, 243, 199))
                }

                detail.isCorrect -> {
                    tvAnswerStatus.text = "Đúng"
                    tvAnswerStatus.setTextColor(Color.rgb(21, 128, 61))
                    tvAnswerStatus.setBackgroundColor(Color.rgb(220, 252, 231))
                }

                else -> {
                    tvAnswerStatus.text = "Sai"
                    tvAnswerStatus.setTextColor(Color.rgb(185, 28, 28))
                    tvAnswerStatus.setBackgroundColor(Color.rgb(254, 226, 226))
                }
            }


            layoutAnswers.removeAllViews()

            answers.forEachIndexed { index, answer ->
                val answerView = TextView(itemView.context)
                val letter = ('A'.code + index).toChar()

                answerView.text = "$letter. ${answer.content}"
                answerView.textSize = 14f
                answerView.setTextColor(Color.rgb(55, 65, 81))
                answerView.setPadding(12, 12, 12, 12)

                val isSelected =
                    answer.answerID == detail.selectedAnswerID

                when {
                    answer.isCorrect -> {
                        answerView.setBackgroundColor(
                            Color.rgb(220, 252, 231)
                        )
                        answerView.setTextColor(
                            Color.rgb(21, 128, 61)
                        )
                    }

                    isSelected -> {
                        answerView.setBackgroundColor(
                            Color.rgb(254, 226, 226)
                        )
                        answerView.setTextColor(
                            Color.rgb(185, 28, 28)
                        )
                    }

                    else -> {
                        answerView.setBackgroundColor(
                            Color.TRANSPARENT
                        )
                    }
                }

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                params.bottomMargin = (
                        6 * itemView.resources.displayMetrics.density
                        ).toInt()

                answerView.layoutParams = params
                layoutAnswers.addView(answerView)
            }

            val explanation = question.explanation

            layoutExplanation.visibility = View.VISIBLE
            tvExplanation.text = if (!explanation.isNullOrBlank()) {
                explanation
            } else {
                "Không có giải thích cho câu hỏi này."
            }
        }

        private fun displayImages(images: List<ImageEntity>) {
            layoutImages.removeAllViews()

            if (images.isEmpty()) {
                layoutImages.visibility = View.GONE
                return
            }

            layoutImages.visibility = View.VISIBLE

            images.forEach { image ->
                val imageView = ImageView(itemView.context)

                imageView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = (
                            8 * itemView.resources.displayMetrics.density
                            ).toInt()
                }

                imageView.adjustViewBounds = true
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                imageView.load(image.imageUrl) {
                    crossfade(true)
                }

                layoutImages.addView(imageView)
            }
        }
    }
}
