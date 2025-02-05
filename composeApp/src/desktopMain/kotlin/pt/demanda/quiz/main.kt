package pt.demanda.quiz

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


fun main() {
    val appName = "Quiz"

    System.setProperty("apple.awt.application.name", appName);

    setupApp()

    application {
        Window(onCloseRequest = ::exitApplication, title = appName) {
            App()
        }
    }
}