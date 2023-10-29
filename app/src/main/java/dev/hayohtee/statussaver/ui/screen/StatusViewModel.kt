package dev.hayohtee.statussaver.ui.screen

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.hayohtee.statussaver.StatusSaverApplication
import dev.hayohtee.statussaver.data.Status
import dev.hayohtee.statussaver.data.StatusRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatusViewModel(private val statusRepository: StatusRepository) : ViewModel() {
    private var uri: Uri? = null
    var uiState by mutableStateOf(StatusUiState())
        private set

    init {
        viewModelScope.launch {
            uri = statusRepository.getStatusDirectoryUri()
            updateUiState(uri)
        }
    }

    private fun updateUiState(uri: Uri?) {
        if (uri == null) {
            uiState = uiState.copy(
                isRecentStatusesLoading = false,
                isSavedStatusesLoading = false
            )
        } else {
            viewModelScope.launch {
                val recentStatuses = async { statusRepository.getRecentStatuses(uri) }
                val savedStatuses = async { statusRepository.getSavedStatuses() }

                uiState = uiState.copy(
                    isDirectoryAccessGranted = true,
                    isSavedStatusesLoading = false,
                    isRecentStatusesLoading = false,
                    recentStatuses = transformStatuses(
                        recentStatuses = recentStatuses.await(),
                        savedStatuses = savedStatuses.await()
                    ),
                    savedStatuses = savedStatuses.await()
                )
            }
        }
    }

    suspend fun fetchRecentStatuses() {
        uiState = uiState.copy(
            recentStatuses = transformStatuses(
                recentStatuses = statusRepository.getRecentStatuses(uri),
                savedStatuses = uiState.savedStatuses
            ),
        )
    }

    private suspend fun transformStatuses(
        recentStatuses: List<Status>,
        savedStatuses: List<Status>
    ): List<Status> {
        return withContext(Dispatchers.Default) {
            recentStatuses.map { recentStatus ->
                recentStatus.copy(
                    isSaved = savedStatuses.any { savedStatus ->
                        savedStatus.name == recentStatus.name
                    }
                )
            }
        }
    }

    suspend fun fetchSavedStatuses() {
        uiState = uiState.copy(savedStatuses = statusRepository.getSavedStatuses())
    }

    suspend fun saveStatus(status: Status) {
        statusRepository.saveStatus(status)
        fetchSavedStatuses()
    }

    fun updateSavedStatus() {
        uiState = uiState.copy(
            isSavedStatusesLoading = true
        )

        viewModelScope.launch {
            uiState = uiState.copy(
                savedStatuses = statusRepository.getSavedStatuses(),
                isSavedStatusesLoading = false
            )
        }
    }

    fun saveStatusDirectoryUri(newUri: Uri) {
        uri = newUri
        viewModelScope.launch {
            uiState = uiState.copy(isDirectoryAccessGranted = true)
            statusRepository.saveStatusDirectoryUri(uri!!)
            updateUiState(uri)
        }
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as StatusSaverApplication)
                StatusViewModel(application.statusRepository)
            }
        }
    }
}