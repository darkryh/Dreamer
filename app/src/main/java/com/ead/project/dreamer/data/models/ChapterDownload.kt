package com.ead.project.dreamer.data.models

import android.app.DownloadManager
import com.ead.project.dreamer.data.utils.DiffUtilEquality


data class ChapterDownload(
    var idChapter : Int,
    var idDownload : Long,
    val idProfile : Int,
    val title : String,
    val cover :String,
    val number : Int,
    var state : Int,
    var current : Int,
    var total : Int
) : DiffUtilEquality {

    fun isInProgress() =  state != DownloadManager.STATUS_FAILED &&
            state != DownloadManager.STATUS_SUCCESSFUL

    override fun equalsHeader(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val chapterDownload: ChapterDownload = other as ChapterDownload
        return this.idDownload == chapterDownload.idDownload
    }

    override fun equalsContent(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val chapterDownload: ChapterDownload = other as ChapterDownload
        return this.title == chapterDownload.title
                && this.state == chapterDownload.state
                && this.current == chapterDownload.current
                && this.total == chapterDownload.total
    }
}