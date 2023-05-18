package com.ead.project.dreamer.app.data.downloads

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.util.Log
import com.ead.project.dreamer.app.data.player.PlayerPreferences
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.Run
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class DownloadsReceiver : BroadcastReceiver() {

    private lateinit var data: Pair<Long,Int>
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    @Inject lateinit var repository: AnimeRepository
    @Inject lateinit var downloadManager : DownloadManager
    @Inject lateinit var playerPreferences: PlayerPreferences


    override fun onReceive(context: Context, intent: Intent) {
        Run.catching {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)
            val query: DownloadManager.Query = DownloadManager.Query()
            query.setFilterById(id)
            data = emptyList<Pair<Long,Int>>().single { it.first == id }
            val chapterToQuery = runBlocking { repository.getChapterFromId(data.second)!! }

            val cursor: Cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {

                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(columnIndex)
                val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val reason = cursor.getInt(columnReason)

                val chapterResult : Chapter = when(status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        chapterToQuery.copy(state = Chapter.STATUS_DOWNLOADED)
                        /*Chapter.removeFromDownloadList(data)*/
                    }
                    DownloadManager.STATUS_PAUSED -> { chapterToQuery.copy(state = Chapter.STATUS_PAUSED) }
                    DownloadManager.STATUS_RUNNING -> { chapterToQuery.copy(state = Chapter.STATUS_RUNNING) }
                    DownloadManager.STATUS_PENDING -> { chapterToQuery.copy(state = Chapter.STATUS_PENDING) }
                    DownloadManager.STATUS_FAILED -> {
                        downloadManager.remove(id)
                        /*Chapter.removeFromDownloadList(data)*/
                        chapterToQuery.copy(state = Chapter.STATUS_FAILED)
                    }
                    else -> { chapterToQuery }
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

                updateCases(chapterResult)

            }
        }
    }

    private fun updateCases(chapter: Chapter) {
        if (chapter.id == (playerPreferences.getChapter()?.id ?: -1)) {
            playerPreferences.setChapter(chapter)
        }
        if (chapter.id == (playerPreferences.getChapter()?.id?:-1)) {
            playerPreferences.setCastingChapter(chapter)
        }
        executor.execute { repository.updateChapterNormal(chapter) }
    }
}