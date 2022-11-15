package com.ead.project.dreamer.data.utils.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.database.Cursor
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.getChapterDownload
import com.ead.project.dreamer.data.models.ChapterDownload
import com.ead.project.dreamer.data.utils.ThreadUtil
import javax.inject.Inject

class DownloadDesigner @Inject constructor(
    private val downloadManager: DownloadManager) {

    private var isExecuting = true
    private lateinit var cursor : Cursor
    private var position = 0
    private var downloadedChapters : MutableLiveData<List<ChapterDownload>> = MutableLiveData()
    private var temporalList : MutableList<ChapterDownload> = mutableListOf()

    fun checkOneTimeSetting() {
        execute {
            checkFirstTime()
        }
    }

    fun onResume() {
        execute {
            try {
                isExecuting = true
                cursor = downloadManager.query(DownloadManager.Query())
                while (cursor.moveToNext() && isExecuting) {
                    if (cursor.isFirst) temporalList.clear()
                    temporalList.add(cursor.getChapterDownload()
                        .apply {
                            this.idDownload = cursor.getId()
                            this.state = cursor.getStatus()
                            this.current = cursor.getCurrentDownloaded()
                            this.total = cursor.getTotalDownloaded()
                        })
                    manageIteration(temporalList)
                }
            } catch (e : Exception) { e.printStackTrace() }
        }
    }

    fun onPause() {
        isExecuting = false
    }

    private fun execute(task : () -> Unit) = ThreadUtil.execute { task() }

    private fun checkFirstTime() {
        if (Constants.isDownloadFirstCheck()) {
            Constants.disableDownloadCheck()
            cursor = downloadManager.query(DownloadManager.Query())
            cursor.removeAll()
        }
    }

    fun getChapters() : MutableLiveData<List<ChapterDownload>> = downloadedChapters

    @SuppressLint("Range")
    private fun Cursor.getStatus() : Int = getInt(getColumnIndex(DownloadManager.COLUMN_STATUS))

    @SuppressLint("Range")
    private fun Cursor.getId() : Long = getLong(getColumnIndex(DownloadManager.COLUMN_ID))

    @SuppressLint("Range")
    private fun Cursor.getCurrentDownloaded() : Int = getInt(getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

    @SuppressLint("Range")
    private fun Cursor.getTotalDownloaded() : Int = getInt(getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

    private fun Cursor.removeAll() { while (moveToNext()) { downloadManager.remove(getId()) } }

    private fun manageIteration(tempList: MutableList<ChapterDownload>) {
        if (cursor.isLast) {
            this@DownloadDesigner.position = 0
            cursor = downloadManager.query(DownloadManager.Query())
            downloadedChapters.postValue(tempList.sortedByDescending { it.idDownload })
            Thread.sleep(1000)
        }
    }
}