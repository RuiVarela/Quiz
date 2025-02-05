package pt.demanda.quiz

import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pt.demanda.quiz.services.LocalDatabase
import pt.demanda.quiz.services.LocalPreferences
import pt.demanda.quiz.services.QuestionsService
import pt.demanda.quiz.services.buildHttpClient
import pt.demanda.quiz.services.buildLocalDatabase
import pt.demanda.quiz.ui.page.QuestionListPageViewModel
import pt.demanda.quiz.ui.page.QuizPageViewModel


val appModuleDi = module {
    single<HttpClient> { buildHttpClient() }
    single<QuestionsService> { QuestionsService(client = get()) }
    single<LocalDatabase> { buildLocalDatabase() }
    single<LocalPreferences> { LocalPreferences.singleton() }


    //
    // View Models
    //
    viewModelOf(::QuizPageViewModel)
    viewModelOf(::QuestionListPageViewModel)
}