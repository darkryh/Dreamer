package com.ead.project.dreamer.data.database.model.server

import android.webkit.WebView
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.ServerWebClient
import com.ead.project.dreamer.data.network.DreamerWebView
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request


class StreamSB(embeddedUrl:String) : Server(embeddedUrl) {

    private lateinit var rawServers : MutableList<String>

    override fun onPreExtract() {
        super.onPreExtract()
        player = Player.StreamSb
        rawServers = mutableListOf()
    }

    override fun onExtract() {
        super.onExtract()
        try {
            url = fixUrl(url)
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()

            val host = response.request.url.host
            val totalData: List<String> = PatternManager.multipleMatches(
                response.body!!.string(),
                "onclick=\"download_video(.*?)\""
            )

            for (data in totalData) {
                val dividedData = fixPreviewUrl(data)
                val fileId = dividedData[0]
                val fileMode = dividedData[1]
                val fileHash = dividedData[2]
                rawServers.add( "https://" + host + "/dl?op=download_orig" +
                "&id=" + fileId +
                "&mode=" + fileMode +
                "&hash=" + fileHash )
            }
            url = rawServers.first()
            initWebView()
            handleDownload(CONNECTION_MIDDLE_STABLE)
            releaseWebView()
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun initWebView() {
        runUI {
            webView = DreamerWebView(DreamerApp.INSTANCE)
            webView?.webViewClient = object : ServerWebClient(webView) {
                override fun onPageLoaded(view: WebView?, url: String?) {
                    super.onPageLoaded(view, url)
                    view?.let { it ->
                        when(timesLoaded) {
                            1 -> { it.evaluateJavascript(skipCaptcha()) {} }
                            2 -> {
                                it.evaluateJavascript(getDownloadScript()) { data ->
                                    this@StreamSB.url = data.removePrefix("\"").removeSuffix("\"")
                                    webView?.isLoading = false
                                }
                            }
                        }
                    }
                }
            }
            webView?.loadUrl(url)
        }
    }

    private fun skipCaptcha() = "document.getElementsByClassName('g-recaptcha')[0].click();"

    private fun getDownloadScript() =
        "let data = document.getElementsByTagName('span')[0].lastElementChild; " +
                "(function() { return data.getAttribute('href'); })(); "

    private fun fixUrl(url : String) = url.replace("/e/","/d/")

    private fun fixPreviewUrl(url : String) = url.removePrefix("(").removeSuffix(")")
            .replace("'","").split(",")

}