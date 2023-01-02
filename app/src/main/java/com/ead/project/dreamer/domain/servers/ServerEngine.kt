package com.ead.project.dreamer.domain.servers

import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebView
import com.ead.project.dreamer.data.commons.Tools.Companion.load
import com.ead.project.dreamer.data.commons.Tools.Companion.onDestroy
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.network.DreamerWebView
import javax.inject.Inject

open class ServerEngine @Inject constructor(
    private val context: Context,
    private val getServerResultToArray: GetServerResultToArray
) {

    private var webView : DreamerWebView?=null
    private lateinit var chapter: Chapter
    private lateinit var timeoutTask: () -> Unit

    private fun getWebView() : DreamerWebView = webView?: DreamerWebView(context).also {
        webView = it
        settingWebViewScrap { timeoutTask() }
    }

    operator fun invoke(timeoutTask : () -> Unit,chapter: Chapter) {
        this.chapter = chapter
        this.timeoutTask = timeoutTask
        loadWebView()
    }

    private fun settingWebViewScrap(timeoutTask : () -> Unit) {
        getWebView().webViewClient = object : DreamerClient() {
            override fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onTimeout(view, url, favicon)
                safeRun { timeoutTask() }
            }

            override fun onPageLoaded(view: WebView?, url: String?) {
                super.onPageLoaded(view, url)
                safeRun {
                    getWebView().evaluateJavascript(DreamerWebView.getServerScript()) { getServerList(it) }
                }
            }
        }
    }

    private fun loadWebView() = getWebView().load(chapter.reference)

    open fun getServerList(it : String) : List<String> = getServerResultToArray(it)

    fun onDestroy() {
        webView?.onDestroy()
        webView = null
    }

    private fun safeRun(task: () -> Unit) { try { task() } catch (e: Exception) { e.printStackTrace() } }

}