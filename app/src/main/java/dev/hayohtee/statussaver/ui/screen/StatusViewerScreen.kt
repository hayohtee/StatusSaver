package dev.hayohtee.statussaver.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import dev.hayohtee.statussaver.R
import dev.hayohtee.statussaver.ui.theme.StatusSaverTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusViewerScreen(
    statusUri: Uri,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.navigate_back
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black,
        contentColor = Color.White,
        modifier = modifier
    ) { innerPadding ->
        val context = LocalContext.current
        val mimeType = context.contentResolver.getType(statusUri) ?: "image"
        val name = statusUri.lastPathSegment ?: ""

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (mimeType.startsWith("image")) {
                StatusImage(
                    statusUri = statusUri,
                    statusName = name,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
fun StatusImage(statusUri: Uri, statusName: String, modifier: Modifier = Modifier) {
    Image(
        painter = rememberAsyncImagePainter(model = statusUri),
        contentDescription = statusName,
        modifier = modifier
    )
}

@Preview
@Composable
fun StatusViewerScreenPreview() {
    StatusSaverTheme {
        StatusViewerScreen(
            statusUri = Uri.parse("https://www.google.com"),
            navigateBack = {}
        )
    }
}