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
import dev.hayohtee.statussaver.data.StatusRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class StatusViewModel(private val statusRepository: StatusRepository) : ViewModel() {
    var uiState by mutableStateOf(StatusUiState())
        private set

    init {
        viewModelScope.launch {
            updateUiState(statusRepository.getStatusDirectoryUri())
        }
    }

    private fun updateUiState(uri: Uri?) {
        viewModelScope.launch {
            val recentStatuses = async { statusRepository.getRecentStatuses(uri) }
//            val savedStatuses = async { statusRepository.getSavedStatuses() }

            uiState = uiState.copy(
                isDirectoryAccessGranted = uri != null,
                isLoading = false,
                recentStatuses = recentStatuses.await(),
//                savedStatuses = savedStatuses.await()
            )
        }
    }

    fun saveStatusDirectoryUri(uri: Uri) {
        viewModelScope.launch {
            uiState = uiState.copy(isDirectoryAccessGranted = true)

            statusRepository.saveStatusDirectoryUri(uri)
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