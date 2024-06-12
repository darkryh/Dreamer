package com.ead.project.dreamer.domain.downloads

import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebView
import com.ead.project.dreamer.app.AppReceiver
import com.ead.project.dreamer.app.data.downloads.DownloadStore
import com.ead.project.dreamer.app.data.downloads.util.DownloadWebView
import com.ead.project.dreamer.app.data.util.system.load
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.utils.Run
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.domain.databasequeries.GetChapter
import com.ead.project.dreamer.domain.servers.GetServerResultToArray
import com.ead.project.dreamer.domain.servers.GetServerUntilFindResource
import com.ead.project.dreamer.domain.servers.GetSortedServers
import com.ead.project.dreamer.domain.servers.ServerScript
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadEngine @Inject constructor(
    private val context: Context,
    private val repository: AnimeRepository,
    private val getChapter: GetChapter,
    private val getServerResultToArray: GetServerResultToArray,
    private val getSortedServers: GetSortedServers,
    private val getServerUntilFindResource: GetServerUntilFindResource,
    private val enqueueDownload: EnqueueDownload,
    private val serverScript: ServerScript,
    private val downloadStore: DownloadStore,
    private val appReceiver: AppReceiver
) {

    private lateinit var webView: DownloadWebView
    private var chapter : Chapter?= null

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        Thread.onUi {
            webView = DownloadWebView(context)
            initWebView()
        }
    }

    operator fun invoke() {
        this.chapter = getEnqueuedChapter()?:return

        if (chapter != null) {
            loadChapter()
        }
        else {
            onCompleted()
        }
    }

    private fun getEnqueuedChapter() : Chapter? = runBlocking {
        getChapter.fromId(downloadStore.getEnqueuedIdChapter()?:-1)
    }

    private fun loadChapter() = webView.load(chapter?.reference?:"null")

    private fun initWebView() {
        webView.webViewClient = object : DreamerClient() {
            override fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onTimeout(view, url, favicon)
                scope.launch {
                    repository.updateChapter(
                        chapter?.copy(state = Chapter.STATUS_FAILED)?:return@launch
                    )
                }
            }

            override fun onPageLoaded(view: WebView?, url: String?) {
                super.onPageLoaded(view, url)
                Run.catching {
                    webView.evaluateJavascript(serverScript()) {
                        getServers(getSortedServers(getServerResultToArray(it),true))
                    }
                }
            }
        }
    }

    private fun getServers(it: List<String>) = scope.launch (Dispatchers.IO) {
        prepareDownload(withContext(Dispatchers.Default) {
            getServerUntilFindResource.fromCoroutine(it)
        })
    }

    private fun prepareDownload(serverList: List<Server>) {
        Run.catching {
            var isDownloadFailed = true

            for (server in serverList) {
                enqueueDownload(server.videoList.last().directLink)
                isDownloadFailed = false
                break
            }

            if (isDownloadFailed) restart()
        }
    }

    private fun enqueueDownload(url : String) {
        enqueueDownload(chapter,url)
        restart()
    }

    fun restart() = invoke()

    fun isLoading() : Boolean = (webView.webViewClient  as DreamerClient).isLoading

    private fun onCompleted() {
        AppReceiver.unregister(context,appReceiver)
    }

}