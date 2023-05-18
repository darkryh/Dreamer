package com.ead.project.dreamer.app.data.util.system

import android.app.DownloadManager
import android.database.Cursor


fun DownloadManager.resetDownloads() {
    val cursor = getAllQuery()
    while (cursor.moveToNext()) {
        remove(cursor.getId())
    }
}

fun DownloadManager.getAllQuery() : Cursor {
    return query(DownloadManager.Query())
}