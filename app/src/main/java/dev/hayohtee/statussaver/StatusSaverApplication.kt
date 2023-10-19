package dev.hayohtee.statussaver

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dev.hayohtee.statussaver.data.StatusRepository

private const val STATUS_SAVER_PREFERENCE_NAME = "status_saver_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = STATUS_SAVER_PREFERENCE_NAME
)
class StatusSaverApplication: Application() {
    lateinit var statusRepository: StatusRepository

    override fun onCreate() {
        super.onCreate()
        statusRepository = StatusRepository(dataStore, this)
    }
}