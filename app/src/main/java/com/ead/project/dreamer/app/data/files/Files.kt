package com.ead.project.dreamer.app.data.files

import android.os.Environment
import com.ead.project.dreamer.data.database.model.Chapter
import java.io.File

object Files {

    private const val MAIN = "Mc files"
    private const val SERIES = "series"
    private val DIRECTORY_DOWNLOADS : String = Environment.DIRECTORY_DOWNLOADS

    val mainFile = getMainFolder()
    val seriesFile = File(mainFile, SERIES)

    val DirectoryDownloadsFile : File =
        Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)

    fun getFile(chapter: Chapter) : File {
        return File(getChapterRoute(chapter))
    }

    fun getChapterRoute(chapter: Chapter) : String {
        return seriesFile.absolutePath + "/" + chapter.title + "/" + chapter.title +
                " Capítulo ${chapter.number}" +".mp4"
    }

    private fun getMainFolder() : File {
        return File(
            DirectoryDownloadsFile,
            MAIN
        )
    }

}