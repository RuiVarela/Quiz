package pt.demanda.quiz

import androidx.compose.runtime.Composable
import androidx.room.RoomDatabase
import pt.demanda.quiz.services.LocalDatabase


interface Platform {
    val platformName: String

    val version: String get() = (if(isDebug) "d" else "v") + QuizBuildConfig.VERSION

    val isDebug: Boolean

    val workingFolder: String

    fun databaseBuilder(): RoomDatabase.Builder<LocalDatabase>
}

expect fun getPlatform(): Platform


//
// this should be replace with a real implementation
// when jetpack compose implements it cross platform
//
@Composable
expect fun BackHandler_PolyFill(onBack: () -> Unit)