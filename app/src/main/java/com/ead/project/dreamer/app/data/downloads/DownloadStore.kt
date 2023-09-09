package com.ead.project.dreamer.app.data.downloads

import android.app.DownloadManager
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.app.data.util.system.downloadItem
import com.ead.project.dreamer.app.data.util.system.getCurrentDownloaded
import com.ead.project.dreamer.app.data.util.system.getId
import com.ead.project.dreamer.app.data.util.system.getStatus
import com.ead.project.dreamer.app.data.util.system.getTotalDownloaded
import com.ead.project.dreamer.data.models.Download
import com.ead.project.dreamer.data.models.DownloadList
import com.ead.project.dreamer.data.models.DownloadQueue
import com.ead.project.dreamer.data.utils.Run
import com.ead.project.dreamer.domain.databasequeries.GetChapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import java.time.Duration
import javax.inject.Inject

class DownloadStore @Inject constructor(
    private val store : DataStore<DownloadList>,
    private val downloadManager: DownloadManager,
    private val getChapter: GetChapter,
) {

    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val defaultQuery = DownloadManager.Query()
    private val activesQuery = DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_RUNNING)

    val downloads = flow<List<Download>> {
        val tempDownloads : MutableList<Download> = mutableListOf()
        while (true) {
            val cursor = downloadManager.query(defaultQuery)
            tempDownloads.clear()
            while (cursor.moveToNext()) {
                Run.catching {
                    tempDownloads.add(
                        cursor.downloadItem().apply {
                            id = cursor.getId()
                            state = cursor.getStatus()
                            current = cursor.getCurrentDownloaded()
                            total = cursor.getTotalDownloaded()
                        }
                    )
                }
            }
            emit(tempDownloads)
            delay(Duration.ofMillis(1000))
        }
    }

    val runningDownloads = flow<List<Download>> {
        val tempDownloads : MutableList<Download> = mutableListOf()
        while (true) {
            val cursor = downloadManager.query(activesQuery)
            tempDownloads.clear()
            while (cursor.moveToNext()) {
                Run.catching {
                    tempDownloads.add(
                        cursor.downloadItem().apply {
                            id = cursor.getId()
                            state = cursor.getStatus()
                            current = cursor.getCurrentDownloaded()
                            total = cursor.getTotalDownloaded()
                        }
                    )
                }
            }
            emit(tempDownloads)
            delay(Duration.ofMillis(1000))
        }
    }

    val enqueue  = flow<List<Download>> {
        val tempDownloads : MutableList<Download> = mutableListOf()
        while (true) {
            tempDownloads.clear()
            store.data.first().downloads.forEach {
                val chapter = getChapter.fromId(it.chapterId)?:return@forEach
                tempDownloads.add(
                    Download(
                        0,
                        chapter.id,
                        chapter.title,
                        chapter.number,
                        Download.DOWNLOAD_TYPE_CHAPTER,
                        0,
                        0,
                        0
                    )
                )
            }
            emit(tempDownloads)
            delay(Duration.ofMillis(250))
        }
    }

    fun getDownload(id : Long) : LiveData<Download?> = downloads.map {
        it.firstOrNull { download: Download -> download.id == id } }.asLiveData()

    fun addDownload(downloadId : Long,chapterId : Int) {
        scope.launch {
            store.updateData { downloadList : DownloadList ->
                downloadList.copy(
                    downloads = downloadList.downloads + DownloadQueue(
                        downloadId = downloadId,
                        chapterId = chapterId
                    )
                )
            }
        }
    }

    fun removeDownload(downloadId: Long) {
        scope.launch {
            store.updateData { downloadList : DownloadList ->
                downloadList.copy(
                    downloads = downloadList.downloads.filter { downloadQueue ->
                        downloadQueue.downloadId != downloadId
                    }
                )
            }
        }
    }

    fun removeDownload(chapterId: Int) : Long = runBlocking {
        val downloadId = store.data.first().downloads.firstOrNull{ it.chapterId == chapterId }?.downloadId ?:-9999
        store.updateData { downloadList : DownloadList ->
            downloadList.copy(
                downloads = downloadList.downloads.filter { downloadQueue ->
                    downloadQueue.chapterId != chapterId
                }
            )
        }
        return@runBlocking downloadId
    }

    fun isDownloading(chapterId: Int) : Boolean = runBlocking {
        return@runBlocking downloads.first().any { it.idReference == chapterId } ||
                enqueue.first().any { it.idReference == chapterId }
    }
    suspend fun getEnqueuedIdChapter() : Int? {
        val downloadQueue = store.data.first().downloads.firstOrNull()
        removeDownload(downloadQueue?.downloadId?:return null)

        return downloadQueue.chapterId
    }

}