package com.ead.project.dreamer.app.data.downloads

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.util.Log
import com.ead.project.dreamer.app.App
import com.ead.project.dreamer.app.data.player.PlayerPreferences
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object DownloadHandler {

    lateinit var repository: AnimeRepository
    lateinit var downloadStore: DownloadStore
    lateinit var playerPreferences: PlayerPreferences


    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val downloadManager : DownloadManager = App.Instance.getSystemService(DownloadManager::class.java)

    fun on(intent: Intent) {
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)
        val query: DownloadManager.Query = DownloadManager.Query()

        val cursor = downloadManager.query(query.setFilterById(id))

        if (cursor.moveToFirst()) {
            scope.launch {
                val chapterId = downloadStore.downloads.first().firstOrNull { it.id == id }?.idReference?:return@launch
                val chapter = getChapterFromStatus(cursor, repository.getChapterFromId(chapterId)?:return@launch)
                onPlayerAndUpdate(chapter)
            }
        }
    }

    private fun getChapterFromStatus(cursor: Cursor, chapterToQuery : Chapter) : Chapter {
        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

        onError(cursor)

        return when(cursor.getInt(columnIndex)) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                downloadStore.removeDownload(chapterToQuery.id)
                chapterToQuery.copy(state = Chapter.STATUS_DOWNLOADED)
            }
            DownloadManager.STATUS_PAUSED -> chapterToQuery.copy(state = Chapter.STATUS_PAUSED)
            DownloadManager.STATUS_RUNNING -> chapterToQuery.copy(state = Chapter.STATUS_RUNNING)
            DownloadManager.STATUS_PENDING -> chapterToQuery.copy(state = Chapter.STATUS_PENDING)
            DownloadManager.STATUS_FAILED -> {
                downloadManager.remove(downloadStore.removeDownload(chapterToQuery.id))
                chapterToQuery.copy(state = Chapter.STATUS_FAILED)
            }
            else -> chapterToQuery
        }
    }

    private fun onError(cursor: Cursor) {
        val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)

        when (cursor.getInt(columnReason)) {
            DownloadManager.ERROR_CANNOT_RESUME -> Log.d(ContentValues.TAG, "ERROR_CANNOT_RESUME")
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> Log.d(ContentValues.TAG, "ERROR_DEVICE_NOT_FOUND")
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> Log.d(ContentValues.TAG, "ERROR_FILE_ALREADY_EXISTS")
            DownloadManager.ERROR_FILE_ERROR -> Log.d(ContentValues.TAG, "ERROR_FILE_ERROR")
            DownloadManager.ERROR_HTTP_DATA_ERROR -> Log.d(ContentValues.TAG, "ERROR_HTTP_DATA_ERROR")
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> Log.d(ContentValues.TAG, "ERROR_INSUFFICIENT_SPACE")
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> Log.d(ContentValues.TAG, "ERROR_TOO_MANY_REDIRECTS")
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> Log.d(ContentValues.TAG, "ERROR_UNHANDLED_HTTP_CODE")
            DownloadManager.ERROR_UNKNOWN -> Log.d(ContentValues.TAG, "ERROR_UNKNOWN")
        }
    }

    private suspend fun onPlayerAndUpdate(chapter: Chapter) {
        val currentPlayingChapter = playerPreferences.getChapter()
        val currentCastingPlayingChapter = playerPreferences.getCastingChapter()

        if (chapter.id == currentPlayingChapter?.id) {
            playerPreferences.setChapter(chapter)
        }

        if (chapter.id == currentCastingPlayingChapter?.id) {
            playerPreferences.setCastingChapter(chapter)
        }

        repository.updateChapter(chapter)
    }
}