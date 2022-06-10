package com.ead.project.dreamer.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest


@SuppressLint("SetJavaScriptEnabled")
class DreamerWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?= null,
    defStyle : Int=0,
    defStylerRes: Int=0) : WebView(context,attrs,defStyle,defStylerRes) {

    init {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = false
        this.settings.userAgentString = DreamerRequest.userAgent()
        this.webViewClient = DreamerClient()
    }

    companion object {
        const val BLANK_BROWSER = "about:blank"
        const val TIMEOUT_MS = 10000L
        const val server_Script = "var event = document.createEvent('HTMLEvents');" +
                "event.initEvent('click',true,true);" +
                "var downloads = document.getElementsByClassName('downbtns')[0].children;" +
                "var servers = document.getElementsByClassName('play-video dropdown-item cap');" +
                "var serverList = [];" +
                "" +
                "function clicker(obj) { " +
                "obj.dispatchEvent(event); " +
                "var player = document.getElementsByClassName('embed-responsive-item')[0];" +
                "serverList.push(player.getAttribute('src')); " +
                "}" +
                "" +
                "for(var server of servers) { " +
                "clicker(server) " +
                "}" +
                "for (var download of downloads) {" +
                "serverList.push((download.href));" +
                "}" +
                "(function() { return serverList; })();"
    }

}