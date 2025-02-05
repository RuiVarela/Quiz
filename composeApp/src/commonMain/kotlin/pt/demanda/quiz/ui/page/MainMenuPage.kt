package pt.demanda.quiz.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.stringResource
import pt.demanda.quiz.Route
import pt.demanda.quiz.getPlatform
import pt.demanda.quiz.ui.core.PageTitle
import quiz.composeapp.generated.resources.Res
import quiz.composeapp.generated.resources.developer_menu_title
import quiz.composeapp.generated.resources.highscores
import quiz.composeapp.generated.resources.main_menu_title
import quiz.composeapp.generated.resources.start_quiz

@Composable
fun MainMenuPage(
    navController: NavHostController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        PageTitle(title = stringResource(Res.string.main_menu_title))

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
                MenuOption(stringResource(Res.string.start_quiz)) {
                    navController.navigate(Route.Quiz)
                }

                MenuOption(stringResource(Res.string.highscores)) {
                    navController.navigate(Route.HighScores)
                }

                if (getPlatform().isDebug) {
                    Spacer(
                        modifier = Modifier
                            .height(32.dp)
                    )
                    MenuOption(stringResource(Res.string.developer_menu_title)) {
                        navController.navigate(Route.DeveloperMenu)
                    }
                }
            }
        }


        //
        // Footer
        //
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {

            Text(
                getPlatform().version,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

    }
}