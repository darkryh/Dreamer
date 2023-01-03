package com.ead.project.dreamer.data.models

import android.app.DownloadManager
import com.ead.project.dreamer.data.utils.DiffUtilEquality

data class DownloadItem(
    var id : Long,
    val idReference : Int,
    val title : String,
    val number : Int,
    val type : Int,
    var current : Int,
    var total : Int,
    var state : Int
) : DiffUtilEquality {

    companion object {
        const val DOWNLOAD_TYPE_CHAPTER = 1
        const val DOWNLOAD_TYPE_UPDATE = 2
    }

    fun isInProgress() =  state != DownloadManager.STATUS_FAILED &&
            state != DownloadManager.STATUS_SUCCESSFUL

    override fun equalsHeader(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val downloadItem: DownloadItem = other as DownloadItem
        return this.id == downloadItem.id
    }

    override fun equalsContent(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val downloadItem: DownloadItem = other as DownloadItem
        return this.idReference == downloadItem.idReference
                && this.title == downloadItem.title
                && this.number == downloadItem.number
                && this.type == downloadItem.type
                && this.current == downloadItem.current
                && this.state == downloadItem.state
    }
}
