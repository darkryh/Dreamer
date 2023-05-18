package com.ead.project.dreamer.app.data.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.system.extensions.toast
import java.io.File

object Apk {

    private const val INSTALL_MIME_TYPE = "application/vnd.android.package-archive"
    private const val FILES_PROVIDER_PATH = ".provider"

    fun install(context: Context, apkFile: File) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                FileProvider
                    .getUriForFile(
                        context,
                        context.applicationContext.packageName + FILES_PROVIDER_PATH,
                        apkFile),
                INSTALL_MIME_TYPE
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e : Exception) {
            e.printStackTrace()
            context.toast(context.getString(R.string.warning_error_installing))
        }
    }
}