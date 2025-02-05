package pt.demanda.quiz.services

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import net.thauvin.erik.urlencoder.UrlEncoderUtil
import pt.demanda.quiz.model.Question
import pt.demanda.quiz.model.Quiz

// https://opentdb.com/
// https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/basic-serialization.md

class QuestionsService(private val client: HttpClient) {
    @Serializable
    private data class ApiQuestion(
        val type: String,
        val difficulty: String,
        val category: String,
        val question: String,

        @SerialName("correct_answer")
        val correctAnswer: String,

        @SerialName("incorrect_answers")
        val incorrectAnswers: List<String>
    )

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    private data class ApiResult(
        @SerialName("response_code")
        val responseCode: Int,

        val results: List<ApiQuestion> = emptyList()
    )

    suspend fun getQuiz(questions: Int = 25): Quiz? {
        kotlin.runCatching {
            val url = "https://opentdb.com/api.php?amount=$questions&encode=url3986"

            val response: HttpResponse = client.request(url)
            val json = response.bodyAsText()

            val data = Json.decodeFromString<ApiResult>(json)

            if (data.results.isNotEmpty()) {
                return Quiz(data.results.map { convertQuestion(it) })
            }
        }.onFailure {
            Napier.e("Failed to get quiz", it)
        }

        return null
    }

    private fun filter(input: String): String = UrlEncoderUtil.decode(input)

    private fun convertQuestion(input: ApiQuestion) : Question {
        val shuffled = (input.incorrectAnswers + input.correctAnswer).shuffled()
        val found = shuffled.indexOf(input.correctAnswer)

        val options = shuffled.map(::filter)
        return Question(
            id = input.question.hashCode(),
            title = filter(input.question),
            options = options,
            answer = found,
            difficulty = input.difficulty,
            category = input.category
        )
    }
}