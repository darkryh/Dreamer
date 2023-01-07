package com.ead.project.dreamer.data.models.server

import android.webkit.WebView
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.ServerWebClient
import com.ead.project.dreamer.data.network.DreamerWebView

class Mediafire(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onExtract() {
        try {
            initWeb()
            handleDownload(CONNECTION_STABLE)
            releaseWebView()
        } catch (e : Exception) { e.printStackTrace() }
    }

    private fun initWeb() {
        runUI {
            webView = DreamerWebView(DreamerApp.INSTANCE)
            webView?.webViewClient = object : ServerWebClient(webView) {
                override fun onPageLoaded(view: WebView?, url: String?) {
                    super.onPageLoaded(view, url)
                    view?.let { if (timesLoaded <= 1) it.evaluateJavascript(scriptLoader()) {} }
                }
            }
            complainWebView()
        }
    }

    private fun scriptLoader() = "document.getElementById('downloadButton').click();"
}