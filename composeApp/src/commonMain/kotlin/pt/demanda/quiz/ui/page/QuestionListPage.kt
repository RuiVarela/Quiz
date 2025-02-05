package pt.demanda.quiz.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.aakira.napier.Napier
import org.koin.compose.viewmodel.koinViewModel
import pt.demanda.quiz.Route
import pt.demanda.quiz.ui.core.collectAsLazyPagingItems

@Composable
private fun Item(value: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .sizeIn(
                minWidth = 500.dp, maxWidth = 500.dp,
                minHeight = 50.dp
            ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(text = value, textAlign = TextAlign.Center)
    }
}

@Composable
fun QuestionListPage(
    navController: NavHostController,
    vm: QuestionListPageViewModel = koinViewModel()
) {
    val query = vm.query.collectAsState()
    val pagingItems = vm.questions.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            modifier = Modifier.padding(16.0f.dp).widthIn(max = 500.0f.dp),
            shape = MaterialTheme.shapes.small,
            shadowElevation = 12.dp,
        ) {
            Row(
                modifier = Modifier
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(modifier = Modifier.padding(end = 10.0f.dp), text = "Search")

                OutlinedTextField(query.value,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                    onValueChange = vm::setQuery
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn {

//                if (pagingItems.loadState.refresh == LoadState.Loading) {
//                    item {
//                        Text(
//                            text = "Waiting for items to load from the backend",
//                            modifier = Modifier.fillMaxWidth()
//                                .wrapContentWidth(Alignment.CenterHorizontally)
//                        )
//                    }
//                }

                items(count = pagingItems.itemCount) { index ->
                    val item = pagingItems[index] ?: return@items
                    Item(value = item.title) {
                        Napier.d("id ${item.id} clicked")

                        navController.navigate(Route.TestQuestion(item))
                    }
                }

//                if (pagingItems.loadState.append == LoadState.Loading) {
//                    item {
//                        CircularProgressIndicator(
//                            modifier = Modifier.fillMaxWidth()
//                                .wrapContentWidth(Alignment.CenterHorizontally)
//                        )
//                    }
//                }
            }
        }
    }
}
