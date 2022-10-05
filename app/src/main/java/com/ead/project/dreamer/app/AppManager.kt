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

            override fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onTimeout(view, url, favicon)
                webView?.loadUrl(BLANK_BROWSER)
                webView?.destroy()
            }

            override fun onPageLoaded(view: WebView?, url: String?) {
                super.onPageLoaded(view, url)
                try { webView?.destroy() }
                catch (e : InterruptedException) { e.printStackTrace() }
            }
        }
    }

    fun onDestroy() {
        webView?.destroy()
        webView = null
    }
}