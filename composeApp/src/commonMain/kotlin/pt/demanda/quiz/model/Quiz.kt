package pt.demanda.quiz.model

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val questions: List<Question>
)

fun emptyQuiz() = Quiz(questions = emptyList())

//
// Sample Data
//
enum class SampleQuiz {
    Normal {
        override fun data() = Quiz(questions = SampleQuestion.entries.map { it.data() })
    },
    Big {
        override fun data() = Quiz(
            questions = listOf(
                SampleQuestion.BigAnswers.data(),
                SampleQuestion.BigTitle.data()
            )
        )
    };

    abstract fun data(): Quiz
}
