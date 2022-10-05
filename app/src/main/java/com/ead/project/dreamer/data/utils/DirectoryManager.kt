package com.ead.project.dreamer.data.utils

import android.os.Environment
import com.ead.project.dreamer.data.commons.Tools.Companion.manageFolder
import java.io.File

class DirectoryManager {

    companion object {

        const val mainFolder = "Dreamer files"
        const val seriesFolder = "Series"

        private fun getMainFolder() : File = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            , mainFolder)

        fun getSeriesFolder() = File(getMainFolder().absolutePath, seriesFolder)

        fun initDirectories() {
            val root = getMainFolder()
            root.manageFolder()
            val series = getSeriesFolder()
            series.manageFolder()
        }
     }
}