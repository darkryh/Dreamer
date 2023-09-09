package com.ead.project.dreamer.app.data.files

import android.os.Environment
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Update
import java.io.File

object Files {

    private const val MAIN = "Dreamer Files"
    private const val SERIES = "series"
    private const val UPDATES = "updates"
    private const val CHAPTER = "Capítulo"
    private val DIRECTORY_DOWNLOADS : String = Environment.DIRECTORY_DOWNLOADS

    val mainFile get() = getMainFolder()
    val seriesFile get() = File(mainFile, SERIES)
    val updatesFile get() = File(mainFile, UPDATES)

    val DirectoryDownloadsFile : File =
        Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)

    fun getFile(chapter: Chapter) : File {
        return File(getChapterRoute(chapter))
    }

    fun getChapterRoute(chapter: Chapter) : String {
        return seriesFile.absolutePath + "/" + chapter.title + "/" + chapter.title + " $CHAPTER ${chapter.number}" +".mp4"
    }

    fun getChapterSubPath(chapter: Chapter) : String {
        return  mainFile.name + "/" + seriesFile.name + "/"  + chapter.title + "/" + chapter.title + " $CHAPTER ${chapter.number}" +".mp4"
    }

    fun getUpdateRoute(update: Update) : String {
        return updatesFile.absolutePath + "/" + update.title + "_" + update.version + ".apk"
    }

    fun getUpdateSubPath(update: Update) : String {
        return  mainFile.name + "/" + updatesFile.name + "/" + update.title + "_" + update.version + ".apk"
    }

    private fun getMainFolder() : File {
        return File(
            DirectoryDownloadsFile,
            MAIN
        )
    }

}