package com.ead.project.dreamer.app.data.util.system

import android.app.DownloadManager
import android.database.Cursor
import android.os.Environment
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Download
import com.ead.project.dreamer.data.models.Update
import com.google.gson.Gson


fun DownloadManager.resetDownloads() {
    val cursor = getAllQuery()
    while (cursor.moveToNext()) {
        remove(cursor.getId())
    }
}

fun DownloadManager.getAllQuery() : Cursor {
    return query(DownloadManager.Query())
}

fun DownloadManager.Request.configure(gson: Gson, chapter: Chapter, subPath: String) : DownloadManager.Request {
    return this
        .setDescription(
            gson.toJson(
                Download(0,chapter.id,chapter.title,chapter.number, Download.DOWNLOAD_TYPE_CHAPTER,0,0,0)
            )
        )
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,subPath)
}

fun DownloadManager.Request.configure(gson: Gson, update: Update, subPath: String) : DownloadManager.Request {
    return this
        .setDescription(
            gson.toJson(
                Download(0,-1,"${update.title}_${update.version}",0, Download.DOWNLOAD_TYPE_UPDATE,0,0,0)
            )
        )
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,subPath)
}