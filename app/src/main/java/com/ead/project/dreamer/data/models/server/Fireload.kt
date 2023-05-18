package com.ead.project.dreamer.data.models.server

import android.webkit.WebView
import com.ead.project.dreamer.app.App
import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.ServerWebClient
import com.ead.project.dreamer.data.network.DreamerWebView

class Fireload (embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.Fireload
    }

    override fun onExtract() {
        try {
            initWeb()
            handleDownload(CONNECTION_UNSTABLE)
            releaseWebView()
        } catch (e : Exception) { e.printStackTrace() }
    }

    private fun initWeb() {
        runUI {
            webView = DreamerWebView(App.Instance)
            webView?.webViewClient = object : ServerWebClient(webView) {

                override fun onPageLoaded(view: WebView?, url: String?) {
                    super.onPageLoaded(view, url)
                    view?.let { if (timesLoaded == 1) it.evaluateJavascript(scriptLoader()) {} }
                }
            }
            complainWebView()
        }
    }

    private fun scriptLoader() = "setTimeout(() => { " +
            "document.getElementsByClassName('dl-button')[0].click(); " +
            "}, '4000');"

}