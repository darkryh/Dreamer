package com.ead.project.dreamer.data.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.webkit.WebView
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.commons.Tools.Companion.isChapterNotInProgress
import com.ead.project.dreamer.data.commons.Tools.Companion.load
import com.ead.project.dreamer.data.commons.Tools.Companion.notContains
import com.ead.project.dreamer.data.commons.Tools.Companion.onDestroy
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoChecker
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.network.DreamerWebView
import com.ead.project.dreamer.data.network.DreamerWebView.Companion.getServerScript
import com.ead.project.dreamer.data.utils.receiver.DownloadsReceiver
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject


open class DownloadManager @Inject constructor(
    private val context: Context,
    private val downloadManager: DownloadManager,
    private val repository: AnimeRepository
) {

    private var chapterList: MutableList<Chapter> = mutableListOf()
    private var webView : DreamerWebView?= null
    private var idDownload =  0L
    private var chapter : Chapter?= null
    private var broadcastReceiver : BroadcastReceiver?= null
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var downloadList : List<Int> = arrayListOf()

    fun init(dataList: List<Chapter>, isFilteredStatus : Boolean = true) {
        val filteredList = if (isFilteredStatus) filterData(dataList) else filterData(filterStatus(dataList))
        if (filteredList.isNotEmpty()) {
            chapterList.addAll(filteredList)
            runOnUI {
                DreamerApp.showShortToast("Empezando Descarga.")
                if (webView == null) settingDownload()
            }
        } else runOnUI { DreamerApp.showShortToast("Todos los capítulos descargados.") }
    }

    fun init(chapter: Chapter) {
        when (chapter.downloadState) {
            Chapter.STATUS_INITIALIZED -> {
                if (isChapterNotInProgress(chapter)) addChapterDownload(chapter,"Empezando descarga.")
                else runOnUI { DreamerApp.showShortToast("Capítulo descarga en progreso.") }
            }
            Chapter.STATUS_RUNNING -> { runOnUI { DreamerApp.showShortToast("Capítulo descarga en progreso.") } }
            Chapter.STATUS_PENDING -> { runOnUI { DreamerApp.showShortToast("Capítulo descarga pendiente.") } }
            Chapter.STATUS_PAUSED -> { runOnUI { DreamerApp.showShortToast("Capítulo descarga en pausa.") } }
            Chapter.STATUS_FAILED -> {
                val data = Chapter.getDownloadList().singleOrNull{ it.second == chapter.id }
                data?.let {
                    downloadManager.remove(it.first)
                    Chapter.removeFromDownloadList(it)
                }
                addChapterDownload(chapter,"Capítulo descarga fallida, Reitendando descarga.")
            }
            Chapter.STATUS_COMPLETED -> { runOnUI { DreamerApp.showShortToast("Capítulo descarga completa.") } }
        }
    }

    private fun filterData(dataList: List<Chapter>) : List<Chapter> {
        val temporalList : MutableList<Chapter> = mutableListOf()
        downloadList = Chapter.getDownloadList().map { it.second }
        for (data in dataList) if (isChapterNotInProgress(data)) temporalList.add(data)
        return temporalList
    }

    private fun filterStatus(dataList: List<Chapter>) = dataList.filter {
        it.downloadState == Chapter.STATUS_INITIALIZED || it.downloadState == Chapter.STATUS_FAILED }

    private fun settingDownload() {
        webView = DreamerWebView(DreamerApp.INSTANCE)
        webView?.webViewClient = object : DreamerClient() {
            override fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onTimeout(view, url, favicon)
                chapter?.let {
                    it.downloadState = Chapter.STATUS_FAILED
                    executor.execute { repository.updateChapterNormal(it) }
                }
                runNextChapter()
            }

            override fun onPageLoaded(view: WebView?, url: String?) {
                super.onPageLoaded(view, url)
                try {
                    webView?.evaluateJavascript(getServerScript()) {
                        val list : MutableList<String> = mutableListOf()
                        for (string in VideoChecker
                            .getSorterServerList(Tools.stringRawArrayToList(it),true)) {
                            list.add(Tools.embedLink(string))
                        }
                        getData(list)
                    }
                } catch (e : Exception) { e.printStackTrace() }
            }

        }
        startDownload()
    }

    private fun startDownload() {
        chapter = getChapterFromList()
        if (broadcastReceiver == null) {
            broadcastReceiver = DownloadsReceiver()
            context.registerReceiver(broadcastReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
        webView?.load(chapterList.first().reference)
    }

    private fun getChapterFromList() : Chapter? = try { chapterList.first() }
    catch (e : Exception) { null }

    private fun getData(it: List<String>) {
        scope.launch (Dispatchers.IO) {
            prepareDownload(withContext(Dispatchers.Default) { ServerManager.getData(it) })
        }
    }

    private fun addChapterDownload(chapter: Chapter,warningMessage : String) {
        if (isChapterNotInProgress(chapter)) {
            chapterList.add(chapter)
            runOnUI {
                DreamerApp.showShortToast(warningMessage)
                if (webView == null) settingDownload()
            }
        }
    }

    private fun prepareDownload(serverList: List<Server>) {
        try {
            var isDownloadFailed = true
            for (server in serverList) {
                if (server.isValidated) {
                    handleDownloads(server.videoList.last().directLink)
                    isDownloadFailed = false
                    break
                }
                else if (server.isConnectionValidated) {
                    handleDownloads(server.videoList.last().directLink)
                    isDownloadFailed = false
                    break
                }
            }
            if (isDownloadFailed) runNextChapter()
        }
        catch ( e : InterruptedException) { e.printStackTrace() }
    }

    private fun handleDownloads(selectedUrl : String) {
        runOnUI {
            chapter?.apply {
                val request = Tools.downloadRequest(this,selectedUrl)
                idDownload = downloadManager.enqueue(request)
                Chapter.addToDownloadList(Pair(idDownload,this.id))
                runNextChapter()
            }
        }
    }

    fun isDataNotInProgress(chapter: Chapter) : Boolean {
        downloadList = Chapter.getDownloadList().map { it.second }
        return  isChapterNotInProgress(chapter)
    }

    private fun isChapterNotInProgress(chapter: Chapter) = chapterList.notContains(chapter)
            && downloadList.notContains(chapter.id)
            && downloadManager.isChapterNotInProgress(chapter)

    private fun runNextChapter() {
        runOnUI {
            if (chapterList.isNotEmpty()) chapterList.removeFirst()
            if (getChapterFromList() != null) startDownload()
            else onCompleted()
        }
    }

    open fun onCompleted() {
        try { context.unregisterReceiver(broadcastReceiver) } catch (e : IllegalArgumentException) { e.printStackTrace()}
        onDestroy()
    }

    private fun runOnUI(task: () -> Unit) = ThreadUtil.onUi { task() }

    private fun onDestroy() {
        scope.cancel()
        webView?.onDestroy()
        webView = null
    }
}
