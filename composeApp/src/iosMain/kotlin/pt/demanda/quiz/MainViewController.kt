package pt.demanda.quiz

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.runBlocking
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {

    //
    //TODO: this might not be the proper place
    //
    setupApp()

    return ComposeUIViewController { App() }
}
