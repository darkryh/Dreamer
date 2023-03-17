package com.ead.project.dreamer.data.utils.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.util.Log
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.utils.DirectoryManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InstallerReceiver : BroadcastReceiver() {

    @Inject lateinit var downloadManager : DownloadManager

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        val query: DownloadManager.Query = DownloadManager.Query()
        query.setFilterById(id)

        val cursor: Cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(columnIndex)
            val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
            val reason = cursor.getInt(columnReason)
            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    Tools.installApk(context,DirectoryManager.getUpdateFile())
                    context.unregisterReceiver(this)
                }
                DownloadManager.STATUS_PAUSED -> {}
                DownloadManager.STATUS_RUNNING -> {}
                DownloadManager.STATUS_PENDING -> {}
                DownloadManager.STATUS_FAILED -> {}
            }
            when (reason) {
                DownloadManager.ERROR_CANNOT_RESUME -> { Log.d(TAG, "ERROR_CANNOT_RESUME") }
                DownloadManager.ERROR_DEVICE_NOT_FOUND -> { Log.d(TAG, "ERROR_DEVICE_NOT_FOUND") }
                DownloadManager.ERROR_FILE_ALREADY_EXISTS -> { Log.d(TAG, "ERROR_FILE_ALREADY_EXISTS") }
                DownloadManager.ERROR_FILE_ERROR -> { Log.d(TAG, "ERROR_FILE_ERROR") }
                DownloadManager.ERROR_HTTP_DATA_ERROR -> { Log.d(TAG, "ERROR_HTTP_DATA_ERROR") }
                DownloadManager.ERROR_INSUFFICIENT_SPACE -> { Log.d(TAG, "ERROR_INSUFFICIENT_SPACE") }
                DownloadManager.ERROR_TOO_MANY_REDIRECTS -> { Log.d(TAG, "ERROR_TOO_MANY_REDIRECTS") }
                DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> { Log.d(TAG, "ERROR_UNHANDLED_HTTP_CODE") }
                DownloadManager.ERROR_UNKNOWN -> { Log.d(TAG, "ERROR_UNKNOWN") }
            }
        }
    }

}