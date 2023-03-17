package com.ead.project.dreamer.data.utils.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.util.Log
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class ChaptersReceiver : BroadcastReceiver() {

    private lateinit var data: Pair<Long,Int>
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var chapter: Chapter
    @Inject lateinit var repository: AnimeRepository
    @Inject lateinit var downloadManager : DownloadManager

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)
            val query: DownloadManager.Query = DownloadManager.Query()
            query.setFilterById(id)
            data = Chapter.getDownloadList().single { it.first == id }
            chapter = runBlocking(Dispatchers.IO) { repository.getChapterFromId(data.second)!! }

            val cursor: Cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(columnIndex)
                val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val reason = cursor.getInt(columnReason)
                when(status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        chapter.downloadState = Chapter.DOWNLOAD_STATUS_COMPLETED
                        Chapter.removeFromDownloadList(data)
                    }
                    DownloadManager.STATUS_PAUSED -> { chapter.downloadState = Chapter.DOWNLOAD_STATUS_PAUSED }
                    DownloadManager.STATUS_RUNNING -> { chapter.downloadState = Chapter.DOWNLOAD_STATUS_RUNNING }
                    DownloadManager.STATUS_PENDING -> { chapter.downloadState = Chapter.DOWNLOAD_STATUS_PENDING }
                    DownloadManager.STATUS_FAILED -> {
                        chapter.downloadState = Chapter.DOWNLOAD_STATUS_FAILED
                        downloadManager.remove(id)
                        Chapter.removeFromDownloadList(data)
                    }
                }
                when (reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> { Log.d(TAG, "ERROR_CANNOT_RESUME") }
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> { Log.d(TAG, "ERROR_DEVICE_NOT_FOUND") }
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> { Log.d(TAG, "ERROR_FILE_ALREADY_EXISTS") }
                    DownloadManager.ERROR_FILE_ERROR -> { Log.d(TAG, "ERROR_FILE_ERROR") }
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> { Log.d(TAG, "ERROR_HTTP_DATA_ERROR") }
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> { Log.d(TAG, "ERROR_INSUFFICIENT_SPACE") }
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> { Log.d(TAG, "ERROR_TOO_MANY_REDIRECTS") }
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> { Log.d(TAG, "ERROR_UNHANDLED_HTTP_CODE") }
                    DownloadManager.ERROR_UNKNOWN -> { Log.d(TAG, "ERROR_UNKNOWN") }
                }
            }
            updateCases()
        } catch (e : Exception) { e.printStackTrace() }
    }

    private fun updateCases() {
        if (chapter.id == (Chapter.get()?.id ?: -1)) Chapter.set(chapter)
        if (chapter.id == (Chapter.getCasting()?.id?:-1)) Chapter.setCasting(chapter)
        executor.execute { repository.updateChapterNormal(chapter) }
    }
}