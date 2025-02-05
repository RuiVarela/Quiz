package pt.demanda.quiz

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import pt.demanda.quiz.model.Question
import pt.demanda.quiz.ui.core.fadeEnter
import pt.demanda.quiz.ui.core.fadeExit
import pt.demanda.quiz.ui.core.navSerializableType
import pt.demanda.quiz.ui.core.noneEnter
import pt.demanda.quiz.ui.core.noneExit
import pt.demanda.quiz.ui.core.slideInFromRight
import pt.demanda.quiz.ui.core.slideOutToRight
import pt.demanda.quiz.ui.page.DeveloperPage
import pt.demanda.quiz.ui.page.HighScorePage
import pt.demanda.quiz.ui.page.MainMenuPage
import pt.demanda.quiz.ui.page.QuestionListPage
import pt.demanda.quiz.ui.page.QuestionPage
import pt.demanda.quiz.ui.page.QuestionResult
import pt.demanda.quiz.ui.page.QuizPage
import kotlin.reflect.typeOf


//https://developer.android.com/develop/ui/compose/navigation

// serialization
// https://medium.com/mercadona-tech/type-safety-in-navigation-compose-23c03e3d74a5

//
// Routes
//
@Serializable
sealed class Route {
    @Serializable
    data object MainMenu : Route()

    @Serializable
    data object DeveloperMenu : Route()

    @Serializable
    data object Quiz : Route()

    @Serializable
    data class QuestionPage(val questionIndex: Int, val question: Question) : Route() {
        companion object {
            val typeMap = mapOf(typeOf<Question>() to navSerializableType<Question>())
            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<QuestionPage>(typeMap)
        }
    }

    @Serializable
    data object QuestionList : Route()

    @Serializable
    data class TestQuestion(val question: Question) : Route()

    @Serializable
    data object HighScores : Route()
}

@Composable
fun Navigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.MainMenu) {
        composable<Route.MainMenu>(
            enterTransition = noneEnter(),
            exitTransition = noneExit()
        ) {
            MainMenuPage(navController)
        }

        composable<Route.QuestionPage>(
            typeMap = Route.QuestionPage.typeMap,
            enterTransition = fadeEnter(),
            exitTransition = fadeExit(),
        ) { entry ->

            val route = entry.toRoute<Route.QuestionPage>()
            QuestionPage(route.questionIndex, route.question, navController)
        }

        composable<Route.Quiz>(
            enterTransition = slideInFromRight(),
            exitTransition = noneExit(),
            popEnterTransition = noneEnter(),
            popExitTransition = slideOutToRight()
        ) { entry ->

            val result = QuestionResult.from(entry.savedStateHandle)
            QuizPage(navController, result)
        }

        composable<Route.DeveloperMenu>(
            enterTransition = slideInFromRight(),
            exitTransition = noneExit(),
            popEnterTransition = noneEnter(),
            popExitTransition = slideOutToRight()
        ) {
            DeveloperPage(navController)
        }

        composable<Route.HighScores>(
            enterTransition = slideInFromRight(),
            exitTransition = noneExit(),
            popEnterTransition = noneEnter(),
            popExitTransition = slideOutToRight()
        ) { entry ->

            HighScorePage(navController)
        }

        composable<Route.QuestionList>(
            enterTransition = slideInFromRight(),
            exitTransition = noneExit(),
            popEnterTransition = noneEnter(),
            popExitTransition = slideOutToRight()
        ) {
            QuestionListPage(navController)
        }

        composable<Route.TestQuestion>(
            typeMap = Route.QuestionPage.typeMap,
            enterTransition = fadeEnter(),
            exitTransition = fadeExit(),
        ) { entry ->

            val route = entry.toRoute<Route.TestQuestion>()
            QuestionPage(0, route.question, navController, showAnswer = true)
        }

    }
}

