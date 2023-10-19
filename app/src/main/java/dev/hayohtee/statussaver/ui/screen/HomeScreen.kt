package dev.hayohtee.statussaver.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hayohtee.statussaver.R
import dev.hayohtee.statussaver.ui.theme.StatusSaverTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: StatusUiState,
    onAccessDirectoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.save_whatsapp_status))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            var tabIndex by remember { mutableIntStateOf(0) }
            val tabsTitle = listOf(
                stringResource(id = R.string.recent),
                stringResource(id = R.string.saved)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = tabIndex) {
                    tabsTitle.forEachIndexed { index, title ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = { tabIndex = index },
                            modifier = Modifier.height(48.dp),
                        ) {
                            Text(
                                text = title.uppercase(),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
                when (tabIndex) {
                    0 -> RecentScreen(
                        uiState = uiState,
                        onAccessDirectoryClick = onAccessDirectoryClick,
                        modifier = Modifier.fillMaxSize()
                    )

                    1 -> SavedScreen(savedStatuses = uiState.savedStatuses)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    StatusSaverTheme {
        HomeScreen(
            uiState = StatusUiState(
                isDirectoryAccessGranted = false,
                recentStatuses = emptyList(),
                savedStatuses = emptyList()
            ),
            onAccessDirectoryClick = {}
        )
    }
}