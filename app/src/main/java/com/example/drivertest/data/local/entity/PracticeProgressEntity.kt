import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "practice_progress"
)
data class PracticeProgressEntity(
    @PrimaryKey
    val chapterNumber: Int,
    val currentQuestionID: Int
)