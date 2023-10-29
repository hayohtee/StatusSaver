package dev.hayohtee.statussaver.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.hayohtee.statussaver.R
import dev.hayohtee.statussaver.data.Status
import dev.hayohtee.statussaver.ui.theme.StatusSaverTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: StatusUiState,
    onAccessDirectoryClick: () -> Unit,
    updateSavedStatus: () -> Unit,
    onSaveStatusClick: (Status) -> Unit,
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
            HomeScreenContent(
                uiState = uiState,
                updateSavedStatus = updateSavedStatus,
                onAccessDirectoryClick = onAccessDirectoryClick,
                onSaveStatusClick = onSaveStatusClick
            )
        }
    }
}

data class TabItem(val title: String)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    uiState: StatusUiState,
    updateSavedStatus: () -> Unit,
    onAccessDirectoryClick: () -> Unit,
    onSaveStatusClick: (Status) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabItems = listOf(
        TabItem(stringResource(id = R.string.recent)),
        TabItem(stringResource(id = R.string.saved))
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { tabItems.size }

    LaunchedEffect(key1 = pagerState.currentPage, key2 = pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }

    LaunchedEffect(key1 = selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabItems.forEachIndexed { index, tab ->
                Tab(
                    selected = (index == selectedTabIndex),
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = tab.title.uppercase(),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { index ->
            when (index) {
                0 -> RecentScreen(
                    uiState = uiState,
                    onAccessDirectoryClick = onAccessDirectoryClick,
                    onSaveStatusClick = onSaveStatusClick,
                    modifier = Modifier.fillMaxSize()
                )

                1 -> SavedScreen(
                    uiState = uiState,
                    updateSavedStatus = updateSavedStatus,
                    modifier = Modifier.fillMaxSize()
                )
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
            onAccessDirectoryClick = {},
            updateSavedStatus = {},
            onSaveStatusClick = {}
        )
    }
}