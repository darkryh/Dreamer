package com.ead.project.dreamer.app.data.downloads

import android.app.DownloadManager
import androidx.datastore.core.DataStore
import com.ead.project.dreamer.app.data.util.system.downloadItem
import com.ead.project.dreamer.app.data.util.system.getCurrentDownloaded
import com.ead.project.dreamer.app.data.util.system.getId
import com.ead.project.dreamer.app.data.util.system.getStatus
import com.ead.project.dreamer.app.data.util.system.getTotalDownloaded
import com.ead.project.dreamer.data.models.Download
import com.ead.project.dreamer.data.models.DownloadList
import com.ead.project.dreamer.data.models.DownloadQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration
import javax.inject.Inject

class DownloadStore @Inject constructor(
    private val store : DataStore<DownloadList>,
    private val downloadManager: DownloadManager
) {

    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val defaultQuery = DownloadManager.Query()

    val downloads = flow<List<Download>> {
        val tempDownloads : MutableList<Download> = mutableListOf()
        while (true) {
            val cursor = downloadManager.query(defaultQuery)
            tempDownloads.clear()
            while (cursor.moveToNext()) {
                this@DownloadStore.catchException {
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
            delay(Duration.ofMillis(1500))
        }
    }

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

    private fun catchException(task: () -> Unit) { try { task() } catch (e: Exception) { e.printStackTrace() } }
}