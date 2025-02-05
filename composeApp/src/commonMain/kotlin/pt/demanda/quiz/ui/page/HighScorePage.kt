package pt.demanda.quiz.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import pt.demanda.quiz.QuizBuildConfig
import pt.demanda.quiz.model.HighScore
import pt.demanda.quiz.services.LocalDatabase
import pt.demanda.quiz.ui.core.PageTitle
import quiz.composeapp.generated.resources.Res
import quiz.composeapp.generated.resources.highscores
import quiz.composeapp.generated.resources.ok

private fun tsText(instant: Instant): String {
    val timezone = TimeZone.currentSystemDefault()
    val dateTime = instant.toLocalDateTime(timezone)

    val format = LocalDateTime.Format {
        dayOfMonth()
        char('-')
        monthNumber()
        char('-')
        year()

        char(' ')

        hour()
        char(':')
        minute()
    }

    return dateTime.format(format)
}

@Composable
private fun Item(record: HighScore) {
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .sizeIn(minWidth = 500.dp, maxWidth = 500.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            val ts = if (record.timestamp.epochSeconds == 0L) "" else tsText(record.timestamp)

            Text(text = ts, textAlign = TextAlign.Start)
            Box(modifier = Modifier.weight(1.0f))
            Text(text = scoreText(record.score), textAlign = TextAlign.End)
        }
    }
}

@Composable
fun HighScorePage(
    navController: NavHostController,
) {
    val database = koinInject<LocalDatabase>()
    val records = remember { mutableStateOf(emptyList<HighScore>()) }

    LaunchedEffect(Unit) {
        val updated = database.highScoreDao().top().toMutableList()
        while (updated.size < QuizBuildConfig.HIGHSCORE_COUNT) {
            updated.add(HighScore(0, 0, Instant.fromEpochSeconds(0)))
        }
        records.value = updated
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        PageTitle(title = stringResource(Res.string.highscores))

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
                items(records.value) {
                    Item(it)
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
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.elevatedButtonColors()
            ) {
                Text(stringResource(Res.string.ok))
            }

        }
    }
}