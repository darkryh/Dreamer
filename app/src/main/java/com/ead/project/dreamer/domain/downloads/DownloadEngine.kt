package com.ead.project.dreamer.domain.downloads

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.webkit.WebView
import com.ead.project.dreamer.app.data.util.system.load
import com.ead.project.dreamer.app.data.util.system.onDestroy
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.network.DreamerWebView
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.app.data.downloads.DownloadsReceiver
import com.ead.project.dreamer.domain.servers.GetServerResultToArray
import com.ead.project.dreamer.domain.servers.GetServers
import com.ead.project.dreamer.domain.servers.GetSortedServers
import com.ead.project.dreamer.domain.servers.ServerScript
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class DownloadEngine @Inject constructor(
    private val context: Context,
    private val repository: AnimeRepository,
    private val tempDownloads: GetTempDownloads,
    private val getServerResultToArray: GetServerResultToArray,
    private val getSortedServers: GetSortedServers,
    private val getServers: GetServers,
    private val createDownload: CreateDownload,
    private val serverScript: ServerScript
) {
    private var webView : DreamerWebView?= null
    private var chapter : Chapter?= null

    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var broadcastReceiver : BroadcastReceiver?= null

    operator fun invoke() {
        this.chapter = tempDownloads.getChapter()
        settingBroadcast()
        runOnUI {
            if (chapter != null) loadChapter()
            else onCompleted()
        }
    }

    fun isNotWorking() = webView == null

    private fun loadChapter() = getWebView().load(chapter!!.reference)

    private fun getWebView() : DreamerWebView = webView?:DreamerWebView(context).also {
        webView = it
        settingWebView()
    }

    fun settingBroadcast() {
        if (broadcastReceiver == null) {
            broadcastReceiver = DownloadsReceiver()
            registerReceiver()
        }
    }

    private fun settingWebView() {
        webView?.webViewClient = object : DreamerClient() {
            override fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onTimeout(view, url, favicon)
                executor.execute {
                    repository.updateChapterNormal(chapter?.copy(
                    state = Chapter.STATUS_FAILED)?:return@execute)
                }
                invoke()
            }

            override fun onPageLoaded(view: WebView?, url: String?) {
                super.onPageLoaded(view, url)
                try {
                    webView?.evaluateJavascript(serverScript()) {
                        getServers(getSortedServers(getServerResultToArray(it),true))
                    }
                } catch (e : Exception) { e.printStackTrace() }
            }

        }
    }

    private fun getServers(it: List<String>) = scope.launch (Dispatchers.IO) {
        prepareDownload(withContext(Dispatchers.Default) { getServers.fromCoroutine(it) }) }

    private fun prepareDownload(serverList: List<Server>) {
        try {
            var isDownloadFailed = true
            for (server in serverList) {
                if (server.isValidated) {
                    launchDownload(server.videoList.last().directLink)
                    isDownloadFailed = false
                    break
                }
                else if (server.isConnectionValidated) {
                    launchDownload(server.videoList.last().directLink)
                    isDownloadFailed = false
                    break
                }
            }
            if (isDownloadFailed) invoke()
        }
        catch ( e : InterruptedException) { invoke() }
    }

    private fun launchDownload(url : String) =
        runOnUI {
            createDownload(chapter,url)
            invoke()
        }

    private fun onCompleted() {
        unregisterReceiver()
        onDestroy()
    }

    private fun registerReceiver() = try { context.registerReceiver(broadcastReceiver,
            IntentFilter(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    } catch (e : IllegalArgumentException) { e.printStackTrace() }

    private fun unregisterReceiver() = try { context.unregisterReceiver(broadcastReceiver) }
    catch (e : IllegalArgumentException) { e.printStackTrace()}

    private fun onDestroy() {
        webView?.onDestroy()
        webView = null
    }

    private fun runOnUI(task: () -> Unit) = Thread.onUi { task() }
}