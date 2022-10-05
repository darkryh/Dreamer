package com.ead.project.dreamer.data.database.model.server

import android.webkit.WebView
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.ServerWebClient
import com.ead.project.dreamer.data.network.DreamerWebView

class Fireload (embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        super.onPreExtract()
        player = Player.Fireload
    }

    override fun onExtract() {
        super.onExtract()
        try {
            initWeb()
            handleDownload(CONNECTION_UNSTABLE)
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

    private fun scriptLoader() = "setTimeout(() => { " +
            "document.getElementsByClassName('dl-button')[0].click(); " +
            "}, '4000');"

}