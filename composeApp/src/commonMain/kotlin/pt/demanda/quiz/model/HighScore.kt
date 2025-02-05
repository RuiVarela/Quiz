package pt.demanda.quiz.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.datetime.Instant
import pt.demanda.quiz.QuizBuildConfig

@Entity
data class HighScore(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int,
    val timestamp: Instant
)

//
// Dao
//
@Dao
interface HighScoreDao {
    @Upsert
    suspend fun insert(record: HighScore): Long

    @Query("SELECT COUNT(*) FROM highscore")
    suspend fun count(): Int

    @Query("SELECT * FROM highscore ORDER BY score DESC, timestamp DESC LIMIT :count")
    suspend fun top(count: Int = QuizBuildConfig.HIGHSCORE_COUNT): List<HighScore>

    @Query("DELETE FROM highscore WHERE id IN (SELECT id FROM highscore ORDER BY score DESC, timestamp DESC LIMIT 1000 OFFSET :count)")
    suspend fun prune(count: Int = QuizBuildConfig.HIGHSCORE_COUNT)
}