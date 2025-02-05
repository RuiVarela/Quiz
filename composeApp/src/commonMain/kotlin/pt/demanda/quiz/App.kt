package pt.demanda.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import pt.demanda.quiz.services.LocalDatabase
import pt.demanda.quiz.services.LocalPreferences
import pt.demanda.quiz.ui.theme.QuizTheme

@Composable
fun AppMainWrapper(content: @Composable BoxScope.() -> Unit) {
    KoinApplication(application = {
        modules(appModuleDi)
    }) {

        // load base data
        val preferences = koinInject<LocalPreferences>()
        val database = koinInject<LocalDatabase>()

        LaunchedEffect(Unit) {
            loadBaseData(preferences, database)
        }

        QuizTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                        //.windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(innerPadding),

                    content = content
                )
            }
        }
    }
}

@Composable
fun App() {
    AppMainWrapper {
        Navigator()
    }
}

fun setupApp() {
    Napier.base(DebugAntilog())
}

suspend fun loadBaseData(preferences: LocalPreferences, database: LocalDatabase) {
    if (!preferences.initialDataLoaded()) {
        Napier.d("Load backup questions")

        withContext(Dispatchers.IO) {
            database.loadBackupQuestions()
        }

        preferences.setInitialDataLoaded()
    }
}