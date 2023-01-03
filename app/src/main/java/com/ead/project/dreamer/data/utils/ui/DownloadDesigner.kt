package com.ead.project.dreamer.data.utils.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.database.Cursor
import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.downloadItem
import com.ead.project.dreamer.data.models.DownloadItem
import com.ead.project.dreamer.data.utils.ThreadUtil
import javax.inject.Inject

class DownloadDesigner @Inject constructor(
    private val downloadManager: DownloadManager) {

    private var isExecuting = true
    private lateinit var cursor : Cursor
    private var position = 0
    private var downloadedItems : MutableLiveData<List<DownloadItem>> = MutableLiveData()
    private var tempList : MutableList<DownloadItem> = mutableListOf()

    fun firstTimeReset() = execute { checkFirstTime() }

    fun onResume() {
        execute {
            try {
                isExecuting = true
                cursor = downloadManager.query(DownloadManager.Query())
                while (cursor.moveToNext() && isExecuting) {
                    if (cursor.isFirst) tempList.clear()
                    tempList.add(cursor.downloadItem()
                        .apply {
                            this.id = cursor.getId()
                            this.state = cursor.getStatus()
                            this.current = cursor.getCurrentDownloaded()
                            this.total = cursor.getTotalDownloaded()
                        })
                    manageIteration(tempList)
                }
            } catch (e : Exception) { e.printStackTrace() }
        }
    }

    fun onPause() { isExecuting = false }

    private fun execute(task : () -> Unit) = ThreadUtil.execute { task() }

    private fun checkFirstTime() {
        if (Constants.isDownloadFirstCheck()) {
            Constants.disableDownloadCheck()
            cursor = downloadManager.query(DownloadManager.Query())
            cursor.removeAll()
        }
    }

    fun getChapters() : MutableLiveData<List<DownloadItem>> = downloadedItems

    @SuppressLint("Range")
    private fun Cursor.getStatus() : Int = getInt(getColumnIndex(DownloadManager.COLUMN_STATUS))

    @SuppressLint("Range")
    private fun Cursor.getId() : Long = getLong(getColumnIndex(DownloadManager.COLUMN_ID))

    @SuppressLint("Range")
    private fun Cursor.getCurrentDownloaded() : Int = getInt(getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

    @SuppressLint("Range")
    private fun Cursor.getTotalDownloaded() : Int = getInt(getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

    private fun Cursor.removeAll() { while (moveToNext()) { downloadManager.remove(getId()) } }

    private fun manageIteration(tempList: MutableList<DownloadItem>) {
        if (cursor.isLast) {
            this@DownloadDesigner.position = 0
            cursor = downloadManager.query(DownloadManager.Query())
            downloadedItems.postValue(tempList.sortedByDescending { it.id })
            Thread.sleep(1000)
        }
    }
}