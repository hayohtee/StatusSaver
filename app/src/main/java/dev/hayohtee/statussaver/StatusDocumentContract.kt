package dev.hayohtee.statussaver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract

class StatusDocumentContract : ActivityResultContract<Uri?, Uri?>() {
    override fun createIntent(context: Context, input: Uri?): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, getStatusContentUri() )
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK && intent != null) {
            intent.data
        } else {
            null
        }
    }
}

fun getStatusContentUri(): Uri {
    return Uri.parse(WHATSAPP_STATUS_FOLDER_TREE_PATH)
}

const val WHATSAPP_STATUS_FOLDER_TREE_PATH = "content://com.android.externalstorage." +
        "documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2F" +
        "com.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"