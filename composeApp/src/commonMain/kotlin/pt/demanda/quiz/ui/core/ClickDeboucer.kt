package pt.demanda.quiz.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import kotlinx.datetime.Clock

@Composable
fun debounceClick(
    onClick: () -> Unit,
    debounceTimeMillis: Long = 500L,
): () -> Unit {
    val lastClickTimeMillis = remember { mutableLongStateOf(value = 0L) }

    return {
        Clock.System.now().toEpochMilliseconds().let { currentTimeMillis ->
            if ((currentTimeMillis - lastClickTimeMillis.value) >= debounceTimeMillis) {
                lastClickTimeMillis.value = currentTimeMillis
                onClick()
            }
        }
    }
}