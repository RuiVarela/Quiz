package pt.demanda.quiz.ui.core

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry

@Composable
fun AnimateViewVisibility(
    modifier: Modifier = Modifier,
    visible: Boolean, // This comes from the mutableState<Boolean>
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = enter,
        exit = exit
    ) {
        content()
    }
}


//
// Pages transitions
//
const val duration = 700

fun slideInFromRight(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) {
    return {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(duration)
        )
    }
}

fun slideOutToRight(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) {
    return {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(duration)
        )
    }
}

fun fadeEnter(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) {
    return {
        fadeIn(animationSpec = tween(duration))
    }
}

fun fadeExit(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) {
    return {
        fadeOut(animationSpec = tween(duration))
    }
}

fun noneEnter(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) {
    return { EnterTransition.None }
}

fun noneExit(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) {
    return { ExitTransition.None }
}

