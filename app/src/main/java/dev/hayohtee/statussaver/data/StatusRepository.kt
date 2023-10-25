package dev.hayohtee.statussaver.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException

class StatusRepository(
    private val dataStore: DataStore<Preferences>,
    private val context: Context
) {
    suspend fun getRecentStatuses(uri: Uri?): List<Status> {
        return withContext(Dispatchers.IO) {
            if (uri == null) return@withContext emptyList()
            val directory = DocumentFile.fromTreeUri(context, uri)
            val files = directory?.listFiles()

            files?.sortedByDescending { it.lastModified() }?.filter {
                it.name!!.endsWith(".jpg") ||
                        it.name!!.endsWith(".png") ||
                        it.name!!.endsWith(".mp4")
            }?.map { file ->
                Status(
                    id = file.name.hashCode().toLong(),
                    uri = file.uri,
                    isVideo = file.name?.endsWith(".mp4") ?: false,
                    isSaved = false
                )
            } ?: emptyList()
        }
    }

    suspend fun getSavedStatuses(): List<Status> {

        val directory = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .resolve(SAVED_STATUS_PATH)

        println("Directory: ${directory.path}")
        if (directory.exists()) {
            println("Directory: ${directory.path} exists")
            return withContext(Dispatchers.IO) {
                val files = directory.listFiles()
                println("Files: ${files?.size}")
                files?.sortedByDescending { it.lastModified() }?.map { file ->
                    Status(
                        id = file.name.hashCode().toLong(),
                        uri = file.toUri(),
                        isVideo = file.name.endsWith(".mp4"),
                        isSaved = true
                    )
                } ?: emptyList()
            }
        }
        return emptyList()
    }

    suspend fun saveStatusDirectoryUri(statusDirectoryUri: Uri) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[STATUS_DIRECTORY_URI_KEY] = statusDirectoryUri.toString()
            }
        }
    }

    suspend fun getStatusDirectoryUri(): Uri? {
        return withContext(Dispatchers.IO) {
            val preferences = dataStore.data.catch {
                if (it is IOException) {
                    Log.e(TAG, "Error reading status directory uri", it)
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.first()

            val uriString = preferences[STATUS_DIRECTORY_URI_KEY]
            return@withContext if (!uriString.isNullOrEmpty()) Uri.parse(uriString) else null
        }
    }

    private companion object {
        val STATUS_DIRECTORY_URI_KEY = stringPreferencesKey("status_directory_uri")
        const val TAG = "StatusRepository"
        const val SAVED_STATUS_PATH = "StatusSaver"
    }
}
