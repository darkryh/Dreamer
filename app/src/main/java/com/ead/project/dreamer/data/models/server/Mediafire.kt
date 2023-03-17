package com.ead.project.dreamer.data.models.server

import android.util.Log
import android.webkit.WebView
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Tools.Companion.delete
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
            webView = DreamerWebView(DreamerApp.Instance)
            webView?.webViewClient = object : ServerWebClient(webView) {
                override fun onPageLoaded(view: WebView?, url: String?) {
                    super.onPageLoaded(view, url)
                    view?.let {
                        view.evaluateJavascript(loadedScript()) { data ->
                            if (data == "null") return@evaluateJavascript

                            var tempUrl = data.delete("\"")
                            if (tempUrl.contains("http://"))
                                tempUrl = tempUrl.replace("http","https")

                            if (!tempUrl.startsWith("https://www.mediafire.com")) {
                                Log.d("testing", "onPageLoaded: $tempUrl")
                                this@Mediafire.url = tempUrl
                                webView?.isLoading = false
                            }
                        }
                    }
                }
            }
            complainWebView()
        }
    }
    private fun loadedScript() =
        "document.getElementById('downloadButton').href;"
}