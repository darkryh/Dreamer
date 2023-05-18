package com.ead.project.dreamer.data.models

import android.app.DownloadManager
import com.ead.project.dreamer.app.data.files.Files
import com.ead.project.dreamer.data.utils.ui.mechanism.EqualsDiffUtil
import java.io.File

data class Download(
    var id : Long,
    val idReference : Int,
    val title : String,
    val number : Int,
    val type : Int,
    var current : Int,
    var total : Int,
    var state : Int
) : EqualsDiffUtil {

    companion object {
        const val DOWNLOAD_TYPE_CHAPTER = 1
        const val DOWNLOAD_TYPE_UPDATE = 2
    }

    fun toApkFile() : File {
        return File(
            Files.DirectoryDownloadsFile,
            "$title.apk"
        )
    }

    fun isInProgress() =  state != DownloadManager.STATUS_FAILED &&
            state != DownloadManager.STATUS_SUCCESSFUL

    override fun equalsHeader(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val download: Download = other as Download
        return this.id == download.id
    }

    override fun equalsContent(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val download: Download = other as Download
        return this.idReference == download.idReference
                && this.title == download.title
                && this.number == download.number
                && this.type == download.type
                && this.current == download.current
                && this.state == download.state
    }
}
