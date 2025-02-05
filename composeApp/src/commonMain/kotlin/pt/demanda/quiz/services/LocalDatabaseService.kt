package pt.demanda.quiz.services

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import pt.demanda.quiz.getPlatform
import pt.demanda.quiz.model.HighScore
import pt.demanda.quiz.model.HighScoreDao
import pt.demanda.quiz.model.Question
import pt.demanda.quiz.model.QuestionDao
import quiz.composeapp.generated.resources.Res

// https://developer.android.com/kotlin/multiplatform/room

//
// custom type converters
//
class Converters {
    @TypeConverter
    fun fromListStringToString(list: List<String>): String = Json.encodeToString(list)

    @TypeConverter
    fun toListStringFromString(data: String): List<String> = Json.decodeFromString(data)


    @TypeConverter
    fun fromInstantToLong(instant: Instant): Long = instant.epochSeconds

    @TypeConverter
    fun toInstantFromLong(instant: Long): Instant =  Instant.fromEpochSeconds(instant)
}

//
// database
//
fun buildLocalDatabase() = getPlatform().databaseBuilder()
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LocalDatabaseConstructor : RoomDatabaseConstructor<LocalDatabase> {
    override fun initialize(): LocalDatabase
}

@TypeConverters(Converters::class)
@Database(
    version = 2,
    entities = [Question::class, HighScore::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@ConstructedBy(LocalDatabaseConstructor::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao

    abstract fun highScoreDao(): HighScoreDao

    @OptIn(ExperimentalResourceApi::class)
    suspend fun loadBackupQuestions() {
        runCatching {
            val json = Res.readBytes("files/import0.json").decodeToString()
            val questions: List<Question> = Json.decodeFromString(json)
            questionDao().insertNonExisting(questions)
        }.onFailure {
            Napier.e("Failed to load backup questions", it)
        }
    }
}
