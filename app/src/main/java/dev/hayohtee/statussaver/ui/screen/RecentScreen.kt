package dev.hayohtee.statussaver.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hayohtee.statussaver.R
import dev.hayohtee.statussaver.data.Status
import dev.hayohtee.statussaver.ui.component.StatusList
import dev.hayohtee.statussaver.ui.theme.StatusSaverTheme

@Composable
fun RecentScreen(
    uiState: StatusUiState,
    onAccessDirectoryClick: () -> Unit,
    onSaveStatusClick: suspend (Status) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (!uiState.isDirectoryAccessGranted && !uiState.isRecentStatusesLoading) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.enable_storage_access),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.enable_read_storage_access_text),
                    textAlign = TextAlign.Center
                )
                Button(onClick = onAccessDirectoryClick) {
                    Text(text = "Access Directory")
                }
            }
        } else {
            StatusList(
                statuses = uiState.recentStatuses,
                onSaveStatusClick = onSaveStatusClick,
                onStatusClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecentScreenPreview() {
    StatusSaverTheme {
        RecentScreen(
            uiState = StatusUiState(),
            onSaveStatusClick = {},
            onAccessDirectoryClick = { }
        )
    }
}