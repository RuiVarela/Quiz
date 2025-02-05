package pt.demanda.quiz.ui.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char

@Composable
fun CountDown(
    time: Int,
    start: Long,
    running: Boolean,
    modifier: Modifier = Modifier,
    onTrigger: () -> Unit
) {

    //
    // timming
    //
    val remaining = remember { mutableIntStateOf(time) }

    LaunchedEffect(running) {
        while (running) {
            delay(10)

            val elapsed = (Clock.System.now().epochSeconds - start).toInt()
            val value = time - elapsed

            if (value < 0) {
                remaining.intValue = 0
                onTrigger()
                break
            } else {
                remaining.intValue = value
            }
        }
    }

    //
    // animation
    //
    val duration = 1000
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val scale = infiniteTransition.animateFloat(
        0.9f, 1.1f,
        infiniteRepeatable(
            tween(duration, easing = LinearOutSlowInEasing),
            RepeatMode.Reverse,
        )
    )
    val alpha = infiniteTransition.animateFloat(
        0.8f, 1.0f,
        infiniteRepeatable(
            tween(duration, easing = LinearEasing),
            RepeatMode.Reverse,
        )
    )

    val format = DateTimeComponents.Format {
        minute()
        char(':')
        second()
    }
    val instant = Instant.fromEpochSeconds(remaining.intValue.toLong())
    val formatted = instant.format(format)

    Text(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            }
            .then(modifier),
        //color = MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.headlineLarge,
        text = formatted
    )
}