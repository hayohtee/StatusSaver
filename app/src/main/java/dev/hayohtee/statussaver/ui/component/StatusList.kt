package dev.hayohtee.statussaver.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hayohtee.statussaver.data.Status

@Composable
fun StatusList(
    statuses: List<Status>,
    onSaveStatusClick: (Status) -> Unit,
    onStatusClick: (Status) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(3)
    ) {
        items(count = statuses.size, key = { statuses[it].id }) { index ->
            val status = statuses[index]
            StatusItem(
                status = status,
                onSaveStatusClick = { onSaveStatusClick(status) },
                onStatusClick = { onStatusClick(status) },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}