package pt.demanda.quiz

import androidx.compose.runtime.Composable
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.demanda.quiz.services.LocalDatabase
import java.io.File
import java.nio.file.FileSystems

class JVMPlatform : Platform {
    override val platformName: String = "Java ${System.getProperty("java.version")}"
    override val isDebug: Boolean = true

    override val workingFolder: String
        get() {
            val userHome = System.getProperty("user.home")
            val folder = ".quiz"
            val path = FileSystems.getDefault().getPath(userHome, folder).toString()
            return path
        }

    override fun databaseBuilder(): RoomDatabase.Builder<LocalDatabase> =
        Room.databaseBuilder<LocalDatabase>(name = File(workingFolder, "quiz.db").absolutePath)
}

actual fun getPlatform(): Platform = JVMPlatform()


@Composable
actual fun BackHandler_PolyFill(onBack: () -> Unit) {
    //BackHandler(onBack = onBack)
}