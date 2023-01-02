package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import com.ead.project.dreamer.data.commons.Tools.Companion.manageFolder
import com.ead.project.dreamer.data.commons.Tools.Companion.toJson
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.DownloadItem
import com.ead.project.dreamer.data.utils.DirectoryManager
import java.io.File
import javax.inject.Inject

class ConfigureDownloadRequest @Inject constructor() {

    operator fun invoke (chapter: Chapter, url : String) : DownloadManager.Request {
        val fileDirectory = File(DirectoryManager.getSeriesFolder().absolutePath, chapter.title)
        fileDirectory.manageFolder()
        return DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("${chapter.title} Cap.${chapter.number}")
            setDescription(DownloadItem(0,chapter.id,chapter.title,chapter.number,0,0,0).toJson())
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                DirectoryManager.mainFolder + "/"  + DirectoryManager.seriesFolder + "/" + chapter.title + "/" + chapter.title + " Capítulo ${chapter.number}" + ".mp4"
            )
        }
    }

    operator fun invoke(title: String, url: String) : DownloadManager.Request {
        return DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(title)
            setDescription(DownloadItem(0,-1,title,-1,0,0,0).toJson())
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"$title.apk")
        }
    }

}