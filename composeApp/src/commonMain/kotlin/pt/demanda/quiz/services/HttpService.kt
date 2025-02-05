package pt.demanda.quiz.services

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

fun buildHttpClient() = HttpClient {
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Napier.v("HTTP Client", null, message)
            }
        }
        level = LogLevel.HEADERS
//        filter { request ->
//            request.url.host.contains("ktor.io")
//        }
//        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
}
