package pt.demanda.quiz.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import pt.demanda.quiz.BackHandler_PolyFill
import pt.demanda.quiz.QuizBuildConfig
import pt.demanda.quiz.model.Question
import pt.demanda.quiz.ui.core.CountDown
import pt.demanda.quiz.ui.core.PageTitle
import pt.demanda.quiz.ui.core.debounceClick
import quiz.composeapp.generated.resources.Res
import quiz.composeapp.generated.resources.give_up

@Serializable
data class QuestionResult(val question: Int, val answer: Int, val remaining: Int) {
    fun to(savedStateHandle: SavedStateHandle) {
        val data = Json.encodeToString(this)
        savedStateHandle[KEY] = data
    }

    companion object {
        const val KEY = "question_result"

        fun clear(savedStateHandle: SavedStateHandle) {
            savedStateHandle.remove<String?>(KEY)
        }

        fun from(savedStateHandle: SavedStateHandle): QuestionResult? {
            val data = savedStateHandle.get<String?>(KEY) ?: return null
            return Json.decodeFromString(data)
        }
    }
}

@Composable
fun MenuOption(value: String, onClick: () -> Unit) = MenuOption(value, false, onClick)

@Composable
fun MenuOption(value: String, accent: Boolean, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = debounceClick(onClick),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .sizeIn(
                minWidth = 500.dp, maxWidth = 500.dp,
                minHeight = 50.dp
            ),
        shape = if (accent) MaterialTheme.shapes.extraSmall else ButtonDefaults.filledTonalShape
    ) {
        Text(text = value, textAlign = TextAlign.Center)
    }
}

@Composable
fun QuestionPage(
    questionIndex: Int,
    question: Question,
    navController: NavHostController,
    showAnswer: Boolean = false
) {
    val time = QuizBuildConfig.QUESTION_TIME
    val startTimestamp = rememberSaveable { Clock.System.now().epochSeconds }
    val clockRunning = remember { mutableStateOf(true) }

    val goBack: () -> Unit = {
        clockRunning.value = false
        navController.previousBackStackEntry?.savedStateHandle?.let(QuestionResult::clear)
        navController.navigateUp()
    }

    // wait for the next compose release
    BackHandler_PolyFill(onBack = goBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        //
        // Header
        //
        PageTitle(title = question.title)

        HorizontalDivider()

        //
        // Body
        //
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn {
                itemsIndexed(question.options) { index, option ->
                    val accent = showAnswer && index == question.answer
                    MenuOption(option, accent, onClick = {
                        clockRunning.value = false
                        val remaining = time - ((Clock.System.now().epochSeconds - startTimestamp).toInt())
                        val result = QuestionResult(questionIndex, index, remaining.coerceAtLeast(0))
                        navController.previousBackStackEntry?.savedStateHandle?.let(result::to)
                        navController.navigateUp()
                    })
                }
            }
        }

        //
        // Footer
        //
        HorizontalDivider()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            OutlinedButton(
                onClick = goBack,
                colors = ButtonDefaults.elevatedButtonColors()
            ) {
                Text(stringResource(Res.string.give_up))
            }

            CountDown(
                time = time,
                start = startTimestamp,
                running = clockRunning.value,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                goBack()
            }
        }
    }
}