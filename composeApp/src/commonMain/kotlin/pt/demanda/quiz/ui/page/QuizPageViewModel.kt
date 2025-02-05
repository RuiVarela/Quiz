package pt.demanda.quiz.ui.page

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pt.demanda.quiz.QuizBuildConfig
import pt.demanda.quiz.Route
import pt.demanda.quiz.model.HighScore
import pt.demanda.quiz.model.Quiz
import pt.demanda.quiz.model.SampleQuiz
import pt.demanda.quiz.model.emptyQuiz
import pt.demanda.quiz.services.LocalDatabase
import pt.demanda.quiz.services.QuestionsService

// https://developer.android.com/codelabs/basic-android-kotlin-compose-viewmodel-and-state#4

@Serializable
sealed class QuizState {
    @Serializable
    data object NotStarted : QuizState()

    @Serializable
    data object Loading : QuizState()

    @Serializable
    data object Loaded : QuizState()

    @Serializable
    data object FailedToLoad : QuizState()

    @Serializable
    data class OnQuestion(val index: Int) : QuizState()

    @Serializable
    data class ShowingQuestionResult(val index: Int, val correct: Boolean) : QuizState()

    @Serializable
    data object Won : QuizState()

    @Serializable
    data object GaveUp : QuizState()
}

class QuizPageViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val questionService: QuestionsService,
    private val database: LocalDatabase
) : ViewModel() {

    companion object {
        const val QUIZ_KEY = "quiz_key"
        const val QUIZ_STATE_KEY = "quiz_state"
        const val CURRENT_QUESTION_KEY = "current_question"
        const val SCORE_KEY = "score"
    }

    //
    // Quiz State
    //
    private val quizStateMutableStateFlow = MutableStateFlow<QuizState>(QuizState.NotStarted)
    val quizStateFlow = quizStateMutableStateFlow.asStateFlow()
    var quizState: QuizState
        get() = quizStateMutableStateFlow.value
        set(value) {
            savedStateHandle[QUIZ_STATE_KEY] = Json.encodeToString(value)
            quizStateMutableStateFlow.value = value
        }

    //
    // Quiz
    //
    private var quiz: Quiz = emptyQuiz()
        set(value) {
            savedStateHandle[QUIZ_KEY] = Json.encodeToString(value)
            field = value
        }

    //
    // score
    //
    val scoreFlow = savedStateHandle.getStateFlow(SCORE_KEY, 0)
    var score: Int
        get() = scoreFlow.value
        set(value) {
            savedStateHandle[SCORE_KEY] = value
        }

    //
    // current Question
    //
    private var currentQuestion: Int
        get() = savedStateHandle.get<Int>(CURRENT_QUESTION_KEY) ?: -1
        set(value) {
            savedStateHandle[CURRENT_QUESTION_KEY] = value
        }


    init {
        //
        // restore
        //
        run {
            // restore complex objects, that cannot be used direclty savedStateHandle
            savedStateHandle.get<String>(QUIZ_KEY)?.let { json ->
                Json.decodeFromString<Quiz>(json).let { quiz = it }
            }

            savedStateHandle.get<String>(QUIZ_STATE_KEY)?.let { json ->
                Json.decodeFromString<QuizState>(json).let { quizState = it }
            }
        }


        //
        // load initial data
        //
        if (quizState == QuizState.NotStarted) {
            viewModelScope.launch {
                loadQuiz()
            }
        }
    }

    private suspend fun loadQuiz() {
        quizState = QuizState.Loading

        val questionCount = QuizBuildConfig.QUIZ_QUESTION_COUNT

//        if (true) {
//            quiz = SampleQuiz.Normal.data()
//            quizState = QuizState.Loaded
//            return
//        }

        // load from network
        if (quizState == QuizState.Loading) {
            val loadedQuiz = questionService.getQuiz(questionCount)
            if (loadedQuiz != null && loadedQuiz.questions.isNotEmpty()) {
                quiz = loadedQuiz
                database.questionDao().insertNonExisting(quiz.questions)
                quizState = QuizState.Loaded
            }
        }

        // load from db
        if (quizState == QuizState.Loading) {
            val questions = database.questionDao().getRandom(questionCount)
            if (questions.isNotEmpty()) {
                quiz = Quiz(questions)
                quizState = QuizState.Loaded
            }
        }

        // something failed
        if (quizState == QuizState.Loading) {
            quizState = QuizState.FailedToLoad
        }
    }

    fun nextQuestion(navController: NavHostController) {
        currentQuestion += 1
        quizState = QuizState.OnQuestion(currentQuestion)

        navController.navigate(Route.QuestionPage(currentQuestion, quiz.questions[currentQuestion]))
    }

    private suspend fun saveHighScore() {
        val highScore = HighScore(score = score, timestamp = Clock.System.now())
        database.highScoreDao().apply {
            insert(highScore)
            prune()
        }
    }

    suspend fun updateQuestion(result: QuestionResult?) {
        val currentState = quizState

        if (currentState !is QuizState.OnQuestion)
            return

        if (result == null) {
            saveHighScore()
            quizState = QuizState.GaveUp
        } else {
            if (currentState.index != result.question) return

            val correct = quiz.questions[currentQuestion].answer == result.answer
            if (correct) {
                score += 1000
                score += result.remaining * 10

                if (result.question == (quiz.questions.size - 1)) {
                    saveHighScore()
                    quizState = QuizState.Won
                    return
                }
            }

            if (!correct) {
                saveHighScore()
            }

            quizState = QuizState.ShowingQuestionResult(result.question, correct)
        }
    }
}