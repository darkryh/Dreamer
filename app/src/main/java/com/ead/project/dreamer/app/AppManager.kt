package com.ead.project.dreamer.app

import android.graphics.Bitmap
import android.webkit.WebView
import com.ead.project.dreamer.data.network.DreamerWebClient
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest

class AppManager {

    private var webView : WebView?=null

    init {
        webView = WebView(DreamerApp.INSTANCE)
        settingJavaScript()
    }

    private fun settingJavaScript() {
        webView?.webViewClient = object : DreamerWebClient(webView!!,DreamerRequest.getExampleLoad()) {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                try {
                    runTimeout {
                        if (timeout) {
                            webView?.loadUrl(BLANK_BROWSER)
                            webView?.destroy()
                        }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                timeout = false
                try {
                    webView?.destroy()
                } catch (e : InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun runTimeout (task: () -> Unit) = ThreadUtil.runInMs(task,DreamerWebClient.TIMEOUT_MS)

    fun onDestroy() {
        webView?.destroy()
    }
}