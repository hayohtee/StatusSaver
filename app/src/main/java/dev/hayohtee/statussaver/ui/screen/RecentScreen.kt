package dev.hayohtee.statussaver.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecentScreen(
    uiState: StatusUiState,
    onAccessDirectoryClick: () -> Unit,
    onSaveStatusClick: suspend (Status) -> Unit,
    fetchRecentStatuses: suspend () -> Unit,
    modifier: Modifier = Modifier
) {

    var refreshing by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            refreshing = true
            coroutineScope.launch {
                delay(1000)
                fetchRecentStatuses()
                refreshing = false
            }
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
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
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecentScreenPreview() {
    StatusSaverTheme {
        RecentScreen(
            uiState = StatusUiState(),
            onSaveStatusClick = {},
            onAccessDirectoryClick = { },
            fetchRecentStatuses = {}
        )
    }
}