package com.ead.project.dreamer.data.utils.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.room.Room
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.AnimeDatabase
import com.ead.project.dreamer.data.database.model.Chapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DownloadsReceiver : BroadcastReceiver() {

    private var database: AnimeDatabase?=null
    private var repository: AnimeRepository?=null
    private var retrofit : Retrofit?=null
    private lateinit var data: Pair<Long,Int>
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var chapter: Chapter
    private lateinit var mContext: Context

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0)?:0
            context?.also { mContext = it }
            val query: DownloadManager.Query = DownloadManager.Query()
            query.setFilterById(id)
            data = Chapter.getDownloadList().single { it.first == id }
            chapter = runBlocking(Dispatchers.IO) { getRepository().getChapterFromId(data.second)!! }

            val downloadManager = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val cursor: Cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(columnIndex)
                val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val reason = cursor.getInt(columnReason)
                when(status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        chapter.downloadState = Chapter.STATUS_COMPLETED
                        Chapter.removeFromDownloadList(data)
                    }
                    DownloadManager.STATUS_PAUSED -> { chapter.downloadState = Chapter.STATUS_PAUSED }
                    DownloadManager.STATUS_RUNNING -> { chapter.downloadState = Chapter.STATUS_RUNNING }
                    DownloadManager.STATUS_PENDING -> { chapter.downloadState = Chapter.STATUS_PENDING }
                    DownloadManager.STATUS_FAILED -> {
                        chapter.downloadState = Chapter.STATUS_FAILED
                        downloadManager.remove(id)
                        Chapter.removeFromDownloadList(data)
                    }
                }
                when(reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> { DreamerApp.showLongToast("ERROR_CANNOT_RESUME") }
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> { DreamerApp.showLongToast("ERROR_DEVICE_NOT_FOUND") }
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> { DreamerApp.showLongToast("ERROR_FILE_ALREADY_EXISTS") }
                    DownloadManager.ERROR_FILE_ERROR -> { DreamerApp.showLongToast("ERROR_FILE_ERROR") }
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> { DreamerApp.showLongToast("ERROR_HTTP_DATA_ERROR") }
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> { DreamerApp.showLongToast("ERROR_INSUFFICIENT_SPACE") }
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> { DreamerApp.showLongToast("ERROR_TOO_MANY_REDIRECTS") }
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> { DreamerApp.showLongToast("ERROR_UNHANDLED_HTTP_CODE") }
                    DownloadManager.ERROR_UNKNOWN -> { DreamerApp.showLongToast("ERROR_UNKNOWN") }
                }
            }
            if (chapter.id == (Chapter.get()?.id ?: -1)) Chapter.set(chapter)
            if (chapter.id == (Chapter.getCasting()?.id?:-1)) Chapter.setCasting(chapter)
            executor.execute { getRepository().updateChapterNormal(chapter) }
        } catch (e : Exception) { e.printStackTrace() }
    }

    private fun getRepository() : AnimeRepository {
        return repository?:AnimeRepository(
            getDatabase(mContext).chapterHomeDao(),
            getDatabase(mContext).animeBaseDao(),
            getDatabase(mContext).animeProfileDao(),
            getDatabase(mContext).chapterDao(),
            getDatabase(mContext).newsItemDao(),
            getRetrofit()
        ).also { repository = it }
    }

    private fun getDatabase(context: Context) : AnimeDatabase = database?:Room.databaseBuilder(
        context,
        AnimeDatabase::class.java,
        AnimeDatabase.DATABASE
    ).build().also { database = it }

    private fun getRetrofit() : Retrofit = retrofit?:Retrofit.Builder()
        .baseUrl(Constants.API_APP)
        .addConverterFactory(GsonConverterFactory.create())
        .build().also { retrofit = it }
}