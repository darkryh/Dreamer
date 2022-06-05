package com.ead.project.dreamer.app

import android.graphics.Bitmap
import android.webkit.WebView
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.network.DreamerWebView
import com.ead.project.dreamer.data.network.DreamerWebView.Companion.BLANK_BROWSER
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest

class AppManager {

    private var webView : DreamerWebView?=null

    init {
        webView = DreamerWebView(DreamerApp.INSTANCE)
        settingWebView()
        webView?.loadUrl(DreamerRequest.getExampleLoad())
    }

    private fun settingWebView() {
        webView?.webViewClient = object : DreamerClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                try {
                    run {
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

    fun onDestroy() {
        webView?.destroy()
    }
}