package dev.hayohtee.statussaver.ui.screen

import dev.hayohtee.statussaver.data.Status

data class StatusUiState(
    val isDirectoryAccessGranted: Boolean = false,
    val recentStatuses: List<Status> = emptyList(),
    val savedStatuses: List<Status> = emptyList(),
    val isLoading: Boolean = true
)
