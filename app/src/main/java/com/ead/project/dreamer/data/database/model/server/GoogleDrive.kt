package com.ead.project.dreamer.data.database.model.server

import android.webkit.WebView
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.ServerWebClient
import com.ead.project.dreamer.data.network.DreamerWebView

class GoogleDrive(embeddedUrl:String) : Server(embeddedUrl) {


    override fun onExtract() {
        super.onExtract()
        try {
            url = fixUrl(url)
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
                    view?.let { if (timesLoaded == 1) it.evaluateJavascript(scriptLoader()) {} }
                }
            }
            complainWebView()
        }
    }

    private fun fixUrl(url : String) = "https://drive.google.com/u/0/uc?id=${getFileId(url)}&export=download"

    private fun getFileId(string: String) = string.substringAfter("/d/")
        .substringBefore("/preview")

    private fun scriptLoader() = "document.getElementById('uc-download-link').click();"
}