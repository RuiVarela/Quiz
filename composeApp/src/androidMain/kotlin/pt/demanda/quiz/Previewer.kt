package pt.demanda.quiz

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import pt.demanda.quiz.model.SampleQuestion
import pt.demanda.quiz.ui.page.QuestionPage

@Composable
private fun TestComponent() {
    //CountDown(30) { Napier.d { "HELLO" } }
    QuestionPage(0, SampleQuestion.Normal00.data(), rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun SimpleComposablePreview() {
    TestComponent()
}