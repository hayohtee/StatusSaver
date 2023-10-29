package dev.hayohtee.statussaver.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.hayohtee.statussaver.StatusDocumentContract
import dev.hayohtee.statussaver.ui.screen.StatusViewModel
import dev.hayohtee.statussaver.ui.screen.HomeScreen

@Composable
fun StatusSaverApp(modifier: Modifier = Modifier) {
    val viewModel = viewModel<StatusViewModel>(factory = StatusViewModel.FACTORY)
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val directoryLauncher = rememberLauncherForActivityResult(
        contract = StatusDocumentContract(),
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.saveStatusDirectoryUri(uri)
        }
    }

    HomeScreen(
        uiState = uiState,
        onAccessDirectoryClick = {
            directoryLauncher.launch(null)
        },
        updateSavedStatus = viewModel::updateSavedStatus,
        onSaveStatusClick = viewModel::saveStatus,
        modifier = modifier
    )
}


