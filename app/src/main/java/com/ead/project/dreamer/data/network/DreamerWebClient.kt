package com.ead.project.dreamer.data.network

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.retrofit.model.discord.Discord
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest


@SuppressLint("SetJavaScriptEnabled")
open class DreamerWebClient(webView: WebView, url: String) : WebViewClient(){

    var timeout = true

    init {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.userAgentString = DreamerRequest.userAgent()
        webView.loadUrl(url)
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val header = request.requestHeaders.toString()
        if (Constants.AUTHORIZATION_VALUE in header) {
            val token = request.requestHeaders["Authorization"]
            if (token != null) {
                DataStore.writeStringAsync(Discord.USER_TOKEN, token)
                Log.d("testing", "token::$token")
            }
        }
        return null
    }

    companion object {

        const val TIMEOUT_MS = 10000L

        //const val API_PROXIES_SCAN = "https://www.proxyscan.io/api/proxy?limit=20&type=https&format=txt"

        const val BLANK_BROWSER = "about:blank"

        const val server_Script = "var event = document.createEvent('HTMLEvents');" +
                "event.initEvent('click',true,true);" +
                "var servers = document.getElementsByClassName('play-video dropdown-item cap');" +
                "var serversHref = document.getElementsByClassName('downbtns')[0].children;" +
                "var serverList = [];" +
                "" +
                "function clicker(obj) {" +
                "obj.dispatchEvent(event);" +
                "var player = document.getElementsByClassName('embed-responsive-item')[0];" +
                "serverList.push(player.getAttribute('src'));" +
                "}" +
                "" +
                "for(var server of servers) {" +
                "clicker(server)" +
                "}" +
                "" +
                "for(var server of serversHref) {" +
                " serverList.push(server.getAttribute('href'));" +
                "}" +
                "" +
                "(function() { return serverList; })();"

    }
}