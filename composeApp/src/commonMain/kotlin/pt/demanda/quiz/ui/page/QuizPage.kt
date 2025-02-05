package pt.demanda.quiz.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.demanda.quiz.ui.core.PageTitle
import quiz.composeapp.generated.resources.Res
import quiz.composeapp.generated.resources.done
import quiz.composeapp.generated.resources.preparing_quiz
import quiz.composeapp.generated.resources.question_correct
import quiz.composeapp.generated.resources.question_incorrect
import quiz.composeapp.generated.resources.quiz_abandoned
import quiz.composeapp.generated.resources.quiz_loading_error
import quiz.composeapp.generated.resources.quiz_winner


@Composable
fun Message(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
    )
}

@Composable
fun DoneButton(navController: NavHostController) {
    Box(modifier = Modifier.padding(top = 32.dp)) {
        OutlinedButton(
            onClick = {
                navController.navigateUp()
            }
        ) {
            Text(stringResource(Res.string.done))
        }
    }
}

fun scoreText(score: Int): String {
    var message = "$score"
    val missing = 6 - message.length
    if (missing > 0) {
        message = "0".repeat(missing) + message
    }
    return message
}


@Composable
fun QuizPage(
    navController: NavHostController,
    questionResult: QuestionResult? = null,
    vm: QuizPageViewModel = koinViewModel()
) {
    val quizState = vm.quizStateFlow.collectAsState()
    val score = vm.scoreFlow.collectAsState()

    val scope = rememberCoroutineScope()

    val loading = quizState.value == QuizState.Loading || quizState.value == QuizState.NotStarted

    LaunchedEffect(Unit) {
        vm.updateQuestion(questionResult)

        vm.quizStateFlow.collect { stage ->
            Napier.d("loadedState collect $stage")
            if (stage == QuizState.Loaded) {
                vm.nextQuestion(navController)
            } else if (stage is QuizState.ShowingQuestionResult && stage.correct) {
                //
                // wait a bit to show the next question
                //
                scope.launch {
                    delay(1500)
                    vm.nextQuestion(navController)
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            if (quizState.value is QuizState.ShowingQuestionResult) {
                val result = quizState.value as QuizState.ShowingQuestionResult
                if (result.correct) {
                    Message(stringResource(Res.string.question_correct))

                } else {
                    Message(stringResource(Res.string.question_incorrect, scoreText(score.value)))
                    DoneButton(navController)
                }

            } else if (quizState.value is QuizState.GaveUp) {

                Message(stringResource(Res.string.quiz_abandoned, scoreText(score.value)))
                DoneButton(navController)

            } else if (quizState.value is QuizState.Won) {

                Message(stringResource(Res.string.quiz_winner, scoreText(score.value)))
                DoneButton(navController)

            } else if (quizState.value == QuizState.FailedToLoad) {

                Message(stringResource(Res.string.quiz_loading_error))
                DoneButton(navController)

            } else {

                AnimatedVisibility(loading) {
                    PageTitle(title = stringResource(Res.string.preparing_quiz))
                }

                AnimatedVisibility(loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

        }
    }
}