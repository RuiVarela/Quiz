package pt.demanda.quiz.services

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path
import okio.Path.Companion.toPath
import pt.demanda.quiz.getPlatform

class LocalPreferences {

    // without a manually set singleton, this crashes with don't keep activities
    companion object {
        private var single: LocalPreferences? = null
        fun singleton(): LocalPreferences = single ?: LocalPreferences().also {
            single = it
        }
    }

    private val store = PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val string = getPlatform().workingFolder + Path.DIRECTORY_SEPARATOR + "local.preferences_pb"
            string.toPath()
        }
    )

    private val keyInitialDataLoaded = booleanPreferencesKey("keyInitialDataLoaded")

    suspend fun initialDataLoaded(): Boolean {
        return store.data.map { preferences ->
            preferences[keyInitialDataLoaded] ?: false
        }.first()
    }

    suspend fun setInitialDataLoaded() {
        store.edit { settings ->
            settings[keyInitialDataLoaded] = true
        }
    }
}




