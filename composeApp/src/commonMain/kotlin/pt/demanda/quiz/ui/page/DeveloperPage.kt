package pt.demanda.quiz.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import pt.demanda.quiz.Route
import pt.demanda.quiz.getPlatform
import pt.demanda.quiz.model.Question
import pt.demanda.quiz.services.LocalDatabase
import pt.demanda.quiz.services.QuestionsService
import pt.demanda.quiz.ui.core.InfoDialog
import pt.demanda.quiz.ui.core.PageTitle
import quiz.composeapp.generated.resources.Res
import quiz.composeapp.generated.resources.developer_menu_title


suspend fun generateInfo(
    database: LocalDatabase,
    vararg args: Pair<String, String> = emptyArray()
): String {
    val questions = database.questionDao().count()


    val builder = StringBuilder()
    for (arg in args) {
        builder.append("${arg.first}: ${arg.second}\n")
    }

    builder.apply {
        append("Version: ${getPlatform().version}\n")
        append("Root: ${getPlatform().workingFolder}\n")
        append("TotalQuestions: $questions\n")
    }

    return builder.toString()
}

@Composable
fun DeveloperPage(
    navController: NavHostController,
) {
    val questionsService = koinInject<QuestionsService>()
    val database = koinInject<LocalDatabase>()
    val scope = rememberCoroutineScope()

    val dialogData = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        PageTitle(title = stringResource(Res.string.developer_menu_title))

        //
        // Body
        //
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {

            Column {
                MenuOption("Question List") {
                    navController.navigate(Route.QuestionList)
                }

                MenuOption("Fetch Remote Questions") {
                    scope.launch {
                        val quiz = questionsService.getQuiz(questions = 5) ?: return@launch
                        val inserted = database.questionDao().insertNonExisting(quiz.questions)

                        dialogData.value = generateInfo(
                            database,
                            "Downloaded Questions: " to quiz.questions.size.toString(),
                            "Inserted Questions: " to inserted.toString()
                        )
                    }
                }

                MenuOption("Load backup questions") {
                    scope.launch {
                        database.loadBackupQuestions()
                        dialogData.value = generateInfo(database)
                    }
                }

                MenuOption("Import Questions") {
                    scope.launch {
                        val loadedQuestions = mutableListOf<Question>()

                        val fs = SystemFileSystem
                        for (i in 0..100) {
                            val filename = Path(getPlatform().workingFolder, "import$i.json")
                            if (!fs.exists(filename)) continue
                            fs.source(filename).use {
                                val json = it.buffered().readByteArray().decodeToString()
                                val questions: List<Question> = Json.decodeFromString(json)
                                loadedQuestions.addAll(questions)
                            }
                            break
                        }

                        val inserted = database.questionDao().insertNonExisting(loadedQuestions)

                        dialogData.value = generateInfo(database, "Loaded" to "$inserted")
                    }
                }

                MenuOption("Dump Questions") {
                    scope.launch {
                        val all = database.questionDao().getAll()
                        val buffer = Buffer().apply {
                            write(Json.encodeToString(all).encodeToByteArray())
                        }

                        val fs = SystemFileSystem
                        var file: String? = null
                        for (i in 0..100) {
                            val filename = Path(getPlatform().workingFolder, "database$i.json")
                            if (fs.exists(filename)) continue
                            fs.sink(filename).use {
                                it.write(buffer, buffer.size)
                            }

                            file = filename.toString()
                            break
                        }

                        dialogData.value = generateInfo(database, "Output" to (file ?: "--"))
                    }
                }

                MenuOption("Show Info") {
                    scope.launch {
                        dialogData.value = generateInfo(database)
                    }
                }
            }
        }
    }

    if (dialogData.value.isNotBlank()) {
        InfoDialog(
            title = "Info",
            message = dialogData.value,
            onDismiss = { dialogData.value = "" }
        )
    }
}