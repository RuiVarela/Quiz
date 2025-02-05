package pt.demanda.quiz

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.demanda.quiz.services.LocalDatabase

class AndroidPlatform : Platform {
    companion object {
        var application: QuizApplication? = null;
    }

    override val platformName: String = "Android ${Build.VERSION.SDK_INT}"

    override val isDebug: Boolean = BuildConfig.DEBUG

    override val workingFolder: String = application!!.let {
        val context = it.applicationContext
        val path = context.filesDir.absolutePath
        return@let path
    }

    override fun databaseBuilder(): RoomDatabase.Builder<LocalDatabase>  {
        val context = application!!.applicationContext
        val file = context.getDatabasePath("quiz.db")
        return Room.databaseBuilder<LocalDatabase>(context = context, name = file.absolutePath)
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun BackHandler_PolyFill(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
}