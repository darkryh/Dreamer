package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import com.ead.project.dreamer.app.data.util.system.manageFolder
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Download
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.gson.Gson
import java.io.File
import javax.inject.Inject

class ConfigureDownloadRequest @Inject constructor(
    private val gson: Gson,
    preferenceUseCase: PreferenceUseCase
) {

    private val filesPreferences = preferenceUseCase.filesPreferences

    operator fun invoke (chapter: Chapter, url : String) : DownloadManager.Request {
        val fileDirectory = File(filesPreferences.series.absolutePath, chapter.title)
        fileDirectory.manageFolder()
        return DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("${chapter.title} Cap.${chapter.number}")
            setDescription(
                gson.toJson(
                    Download(0,chapter.id,chapter.title,chapter.number,Download.DOWNLOAD_TYPE_CHAPTER,0,0,0)
                )
            )
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                filesPreferences.getChapterRoute(chapter)
            )
        }
    }

    operator fun invoke(title: String, url: String) : DownloadManager.Request {
        return DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(title)
            setDescription(
                gson.toJson(Download(0,-1,title,-1,Download.DOWNLOAD_TYPE_UPDATE,0,0,0))
            )
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"$title.apk")
        }
    }

}