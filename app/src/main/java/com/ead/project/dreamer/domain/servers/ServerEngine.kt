package com.ead.project.dreamer.domain.servers

import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebView
import com.ead.project.dreamer.app.data.server.util.ServerWebView
import com.ead.project.dreamer.app.data.util.system.load
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.utils.Run
import com.ead.project.dreamer.data.utils.Thread
import javax.inject.Inject

open class ServerEngine @Inject constructor(
    private val context: Context,
    private val getServerResultToArray: GetServerResultToArray,
    private val serverScript: ServerScript,

) {
    private lateinit var webView: ServerWebView

    private lateinit var chapter: Chapter
    private lateinit var timeoutTask: () -> Unit

    init {
        Thread.onUi {
            webView = ServerWebView(context)
            settingWebViewScrap { timeoutTask() }
        }
    }

    operator fun invoke(timeoutTask : () -> Unit,chapter: Chapter) {
        this.chapter = chapter
        this.timeoutTask = timeoutTask
        loadWebView()
    }

    private fun settingWebViewScrap(timeoutTask : () -> Unit) {
        webView.webViewClient = object : DreamerClient() {
            override fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onTimeout(view, url, favicon)
                Run.catching {
                    timeoutTask()
                }
            }

            override fun onPageLoaded(view: WebView?, url: String?) {
                super.onPageLoaded(view, url)
                Run.catching {
                    webView.evaluateJavascript(serverScript()) { getServerList(it) }
                }
            }
        }
    }

    private fun loadWebView() = webView.load(chapter.reference)

    open fun getServerList(it : String) : List<String> = getServerResultToArray(it)

}