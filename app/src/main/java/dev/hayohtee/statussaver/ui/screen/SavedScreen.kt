package dev.hayohtee.statussaver.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.hayohtee.statussaver.data.Status
import dev.hayohtee.statussaver.ui.theme.StatusSaverTheme


@Composable
fun SavedScreen(savedStatuses: List<Status>, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {

    }
}

@Preview(showBackground = true)
@Composable
fun SavedScreenPreview() {
    StatusSaverTheme {
        SavedScreen(savedStatuses = emptyList())
    }
}