package dev.hayohtee.statussaver.ui.screen

import dev.hayohtee.statussaver.data.Status

data class StatusUiState(
    val isDirectoryAccessGranted: Boolean = false,
    val isRecentStatusesLoading: Boolean = true,
    val isSavedStatusesLoading: Boolean = true,
    val recentStatuses: List<Status> = emptyList(),
    val savedStatuses: List<Status> = emptyList(),
)
