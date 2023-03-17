package com.ead.project.dreamer.app

import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebView
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.network.DreamerWebView
import com.ead.project.dreamer.data.network.DreamerWebView.Companion.BLANK_BROWSER
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest

class ProviderVerifier(context : Context) {

    private var webView : DreamerWebView?=null

    init {
        webView = DreamerWebView(context)
        settingWebView()
        webView?.loadUrl(DreamerRequest.getExampleLoad())
    }

    private fun settingWebView() {
        webView?.webViewClient = object : DreamerClient() {

            override fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onTimeout(view, url, favicon)
                onDestroy()
            }

            override fun onPageLoaded(view: WebView?, url: String?) {
                super.onPageLoaded(view, url)
                try { webView?.destroy() }
                catch (e : InterruptedException) { e.printStackTrace() }
            }
        }
    }

    fun onDestroy() {
        webView?.loadUrl(BLANK_BROWSER)
        webView?.destroy()
        webView = null
    }
}