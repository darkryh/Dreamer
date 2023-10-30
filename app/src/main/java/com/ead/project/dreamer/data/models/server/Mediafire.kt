package com.ead.project.dreamer.data.models.server

import android.webkit.WebView
import com.ead.project.dreamer.app.App
import com.ead.project.dreamer.app.data.util.system.delete
import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.ServerWebClient
import com.ead.project.dreamer.data.network.DreamerWebView

class Mediafire(embeddedUrl:String) : Server(embeddedUrl,Player.Mediafire) {

    override fun onExtract() {
        try {
            initWeb()
            handleDownload(CONNECTION_STABLE)
            releaseWebView()
        } catch (e : Exception) { e.printStackTrace() }
    }

    private fun initWeb() {
        runUI {
            webView = DreamerWebView(App.Instance)
            webView?.webViewClient = object : ServerWebClient(webView) {
                override fun onPageLoaded(view: WebView?, url: String?) {
                    super.onPageLoaded(view, url)
                    view?.apply {
                        evaluateJavascript(loadedScript()) { data ->
                            if (data == "null") {
                                handleJavascriptResult(data,true)
                                return@evaluateJavascript
                            }
                            handleJavascriptResult(data,false)
                        }
                    }
                }
            }
            complainWebView()
        }
    }

    private fun handleJavascriptResult(data : String, shouldBack : Boolean) {
        if (shouldBack) {
            webView?.apply {
                if (canGoBack()) {
                    goBack()
                }
            }
            return
        }
        var tempUrl = data.delete("\"")
        if (tempUrl.contains("http://"))
            tempUrl = tempUrl.replace("http","https")


        if (!tempUrl.startsWith("https://www.mediafire.com")) {
            this@Mediafire.url = tempUrl
            webView?.isLoading = false
        }
        else {
            webView?.loadUrl(url?:return)
        }
    }
    private fun loadedScript() =
        "document.getElementById('downloadButton').href;"
}