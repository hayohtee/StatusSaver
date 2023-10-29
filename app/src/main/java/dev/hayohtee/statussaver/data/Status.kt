package dev.hayohtee.statussaver.data

import android.net.Uri

data class Status(
    val name: String,
    val uri: Uri,
    val isVideo: Boolean,
    val isSaved: Boolean = false,
    val lastModified: Long
)
