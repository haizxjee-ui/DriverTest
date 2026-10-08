import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.drivertest.data.local.entity.QuestionEntity

@Entity(
    tableName = "user_question_progress",
    primaryKeys = ["chapterNumber", "questionID"],
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["questionID"],
            childColumns = ["questionID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserQuestionProgressEntity(
    val chapterNumber: Int,
    val questionID: Int,
    val selectedAnswer: String?,
    val isAnswered: Boolean,
    val isCorrect: Boolean?
)