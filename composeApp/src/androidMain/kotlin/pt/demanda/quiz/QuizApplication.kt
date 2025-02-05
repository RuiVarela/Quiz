package pt.demanda.quiz

import android.app.Application

class QuizApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AndroidPlatform.application = this
        setupApp()
    }
}