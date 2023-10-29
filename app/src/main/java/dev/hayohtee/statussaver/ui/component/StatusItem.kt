package dev.hayohtee.statussaver.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import dev.hayohtee.statussaver.R
import dev.hayohtee.statussaver.data.Status
import kotlinx.coroutines.launch

@Composable
fun StatusItem(
    status: Status,
    onSaveStatusClick: suspend (Status) -> Unit,
    onStatusClick: (Status) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSaved by rememberSaveable(status) { mutableStateOf(status.isSaved) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier
        .size(150.dp, 200.dp)
        .clip(RoundedCornerShape(10))
        .clickable { onStatusClick(status) }
    ) {
        if (status.isVideo) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = status.uri,
                    imageLoader = ImageLoader.Builder(LocalContext.current)
                        .components {
                            add(VideoFrameDecoder.Factory())
                        }.crossfade(true).build()
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else {
            AsyncImage(
                model = status.uri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        AnimatedVisibility(
            visible = !isSaved,
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-5).dp, y = 5.dp),
            exit = fadeOut()
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        onSaveStatusClick(status)
                        isSaved = true
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_download),
                    contentDescription = stringResource(id = R.string.save_status),
                    tint = Color.White
                )
            }
        }


        if (status.isVideo) {
            Icon(
                painter = painterResource(id = R.drawable.ic_video_playback),
                contentDescription = stringResource(id = R.string.play_video),
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
