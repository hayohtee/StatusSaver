package dev.hayohtee.statussaver.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.hayohtee.statussaver.R
import dev.hayohtee.statussaver.ui.component.StatusList
import dev.hayohtee.statussaver.ui.theme.StatusSaverTheme


@Composable
fun SavedScreen(
    uiState: StatusUiState,
    updateSavedStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (!uiState.isSavedStatusesLoading && uiState.savedStatuses.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Saved Status",
                    style = MaterialTheme.typography.titleLarge
                )
                PermissionContent(
                    updateSavedStatus = updateSavedStatus,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            StatusList(
                statuses = uiState.savedStatuses,
                onSaveStatusClick = {},
                onStatusClick = {}
            )
        }
    }
}

@Composable
fun PermissionContent(updateSavedStatus: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isReadAccessGranted by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isReadAccessGranted = isGranted
        if (isGranted) {
            updateSavedStatus()
        }
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !isReadAccessGranted) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.enable_read_storage_access_text),
                textAlign = TextAlign.Center
            )
            Button(onClick = {
                launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }) {
                Text(text = stringResource(id = R.string.enable_storage_access))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedScreenPreview() {
    StatusSaverTheme {
        SavedScreen(
            uiState = StatusUiState(),
            updateSavedStatus = {}
        )
    }
}