package dev.hayohtee.statussaver.ui.screen

import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
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
            } else {
                StatusVideo(
                    statusUri = statusUri,
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

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun StatusVideo(statusUri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(statusUri))
            prepare()
        }
    }
    exoPlayer.playWhenReady = true
    exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun StatusViewerScreenPreview() {
    StatusSaverTheme {
        StatusViewerScreen(
            statusUri = Uri.parse(""),
            navigateBack = {}
        )
    }
}