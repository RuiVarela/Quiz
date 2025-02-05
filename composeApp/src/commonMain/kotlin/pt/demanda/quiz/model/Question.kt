package pt.demanda.quiz.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Question(

    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,

    val title: String,
    val options: List<String>,
    val answer: Int,
    val difficulty: String,
    val category: String,
)


//
// Dao
//
@Dao
interface QuestionDao {
    @Upsert
    suspend fun insert(question: Question): Long

    @Insert
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM question WHERE id = :id")
    suspend fun getById(id: Int): Question?

    @Delete
    suspend fun delete(question: Question)

    @Query("DELETE FROM question WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("SELECT * FROM question")
    suspend fun getAll(): List<Question>

    @Query("SELECT * FROM question ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandom(limit: Int): List<Question>

    @Query("SELECT * FROM question")
    fun getAllPaginated(): PagingSource<Int, Question>

    @Query("SELECT * FROM question WHERE title LIKE '%' || :search || '%'")
    fun searchPaginated(search: String): PagingSource<Int, Question>

    @Query("SELECT COUNT(*) FROM question")
    suspend fun count(): Int

    @Query("SELECT id FROM question WHERE id IN (:ids)")
    suspend fun existingIds(ids: List<Int>): List<Int>


    suspend fun insertNonExisting(questions: List<Question>) : Int {
        val allIds = questions.map { it.id }
        val existingIds = existingIds(allIds)
        val new = questions.filter { !existingIds.contains(it.id) }

        insertAll(new)

        return new.size
    }
}

//
// Sample Data
//
enum class SampleQuestion {
    Normal00 {
        override fun data() = Question(
            id = 0,
            title = "What is the capital of Portugal?",
            options = listOf("Lisbon", "Porto", "Faro", "Coimbra"),
            answer = 0,
            difficulty = "easy",
            category = "world"
        )
    },
    Normal01 {
        override fun data() = Question(
            id = 1,
            title = "What is the capital of Spain?",
            options = listOf("Barcelona", "Madrid", "Seville", "Valencia"),
            answer = 1,
            difficulty = "easy",
            category = "world"
        )
    },
    Normal02 {
        override fun data() = Question(
            id = 2,
            title = "What is the capital of France?",
            options = listOf("Marseille", "Lyon", "Toulouse", "Paris"),
            answer = 3,
            difficulty = "easy",
            category = "world"
        )
    },
    Normal03 {
        override fun data() = Question(
            id = 3,
            title = "What is the capital of Germany?",
            options = listOf("Munich", "Hamburg", "Berlin", "Cologne"),
            answer = 2,
            difficulty = "easy",
            category = "world"
        )
    },


    BigTitle {
        override fun data() = Question(
            id = 4,
            title = "What is the super mega biggeste whoohoo capital of Portugal?",
            options = listOf("Lisbon", "Porto", "Faro", "Coimbra"),
            answer = 0,
            difficulty = "easy",
            category = "world"
        )
    },

    BigAnswers {
        override fun data() = Question(
            id = 5,
            title = "What is the capital of Portugal?",
            options = listOf(
                "Lisbon",
                "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy te",
                "Faro",
                "when an unknown printer took a galley of type and scrambled it "
            ),
            answer = 0,
            difficulty = "easy",
            category = "world"
        )
    };

    abstract fun data(): Question
}
