package pt.demanda.quiz

import androidx.compose.runtime.Composable
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIDevice
import pt.demanda.quiz.services.LocalDatabase
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

class IOSPlatform: Platform {
    override val platformName: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    @OptIn(ExperimentalNativeApi::class)
    override val isDebug: Boolean = kotlin.native.Platform.isDebugBinary
    override val workingFolder: String get() = documentDirectory()

    override fun databaseBuilder(): RoomDatabase.Builder<LocalDatabase> =
        Room.databaseBuilder<LocalDatabase>(name = documentDirectory() + "/quiz.db")
    
}

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun BackHandler_PolyFill(onBack: () -> Unit) {

}