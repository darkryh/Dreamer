package com.ead.project.dreamer.domain.downloads

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.configure
import com.ead.project.dreamer.app.data.util.system.configureFolder
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Update
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.google.gson.Gson
import java.io.File
import javax.inject.Inject

class ConfigureDownload @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    preferenceUseCase: PreferenceUseCase
) {

    private val filesPreferences = preferenceUseCase.filesPreferences

    operator fun invoke (chapter: Chapter, url : String) : DownloadManager.Request {

        val chapterDirectory = File(filesPreferences.series.absolutePath, chapter.title)
        chapterDirectory.configureFolder()

        return DownloadManager.Request(Uri.parse(url))
            .setTitle(context.getString(R.string.title_and_number_short,chapter.title,chapter.number))
            .configure(gson,chapter,filesPreferences.getChapterSubPath(chapter))
    }

    operator fun invoke(update: Update, url: String) : DownloadManager.Request {

        val updateDirectory = filesPreferences.updates
        updateDirectory.configureFolder()

        return DownloadManager.Request(Uri.parse(url))
            .setTitle(context.getString(R.string.title_and_version,update.title,update.version))
            .configure(gson,update,filesPreferences.getUpdateSubPath(update))
    }

}