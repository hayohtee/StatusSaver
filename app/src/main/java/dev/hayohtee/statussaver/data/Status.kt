package dev.hayohtee.statussaver.data

import android.net.Uri

data class Status(
    val id: Long,
    val uri: Uri,
    val isVideo: Boolean,
    val isSaved: Boolean = false
)
