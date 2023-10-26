package dev.hayohtee.statussaver.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.DocumentsContract.Document.COLUMN_DISPLAY_NAME
import android.provider.DocumentsContract.Document.COLUMN_DOCUMENT_ID
import android.provider.DocumentsContract.Document.COLUMN_LAST_MODIFIED
import android.provider.DocumentsContract.Document.COLUMN_MIME_TYPE
import android.util.Log
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException

class StatusRepository(
    private val dataStore: DataStore<Preferences>,
    private val context: Context
) {
    suspend fun getRecentStatuses(treeUri: Uri?): List<Status> {
        return withContext(Dispatchers.IO) {
            if (treeUri == null) return@withContext emptyList()

            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                treeUri,
                DocumentsContract.getTreeDocumentId(treeUri)
            )

            val progression = arrayOf(
                COLUMN_DOCUMENT_ID,
                COLUMN_DISPLAY_NAME,
                COLUMN_MIME_TYPE,
                COLUMN_LAST_MODIFIED
            )

            val selection = "$COLUMN_MIME_TYPE LIKE 'image/%' OR $COLUMN_MIME_TYPE LIKE 'video/%'"
            val sortOrder = "$COLUMN_LAST_MODIFIED DESC"

            val cursor = context.contentResolver.query(
                childrenUri,
                progression,
                selection,
                null,
                sortOrder
            )

            val statuses = mutableListOf<Status>()

            cursor?.use {
                val documentIdIndex = cursor.getColumnIndexOrThrow(COLUMN_DOCUMENT_ID)
                val documentNameIndex = cursor.getColumnIndexOrThrow(COLUMN_DISPLAY_NAME)
                val mimeTypeIndex = cursor.getColumnIndexOrThrow(COLUMN_MIME_TYPE)

                while (cursor.moveToNext()) {
                    val documentId = cursor.getString(documentIdIndex)
                    val documentName = cursor.getString(documentNameIndex)
                    val mimeType = cursor.getString(mimeTypeIndex)

                    val status = Status(
                        id = documentName.hashCode().toLong(),
                        uri = DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId),
                        isVideo = mimeType.startsWith("video/"),
                        isSaved = false
                    )
                    statuses.add(status)
                }
            }

            return@withContext statuses
        }
    }

    suspend fun getSavedStatuses(): List<Status> {

        val directory = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .resolve(SAVED_STATUS_PATH)

        if (directory.exists()) {
            return withContext(Dispatchers.IO) {
                val files = directory.listFiles()
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
