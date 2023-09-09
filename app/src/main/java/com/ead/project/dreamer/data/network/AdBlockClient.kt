package com.ead.project.dreamer.data.network

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.regex.Matcher
import java.util.regex.Pattern


open class AdBlockClient : WebViewClient() {

    private val loadedUrls: Map<String, Boolean> = HashMap()
    private val trustedSites: List<String> = listOf(
        "https://www\\.monoschinos2\\.com/*",
        "https://www\\.yourupload\\.com/*",
        "https://www\\.mp4upload\\.com/*",
        "https://www\\.streamwish\\.to/*",
        "https://www\\.dood\\.com/*",
        "https://www\\.dooood\\.com/*",
        "https://www\\.mixdrop\\.co/*",
        "https://www\\.filemoon\\.sx/*",
        "https://www\\.uqload\\.com/*",
        "https://www\\.mega\\.nz/*")


    private fun isUrlPlayer(urlSender: String): Boolean {
        for (url in this.trustedSites) {
            val pattern: Pattern = Pattern.compile(url, Pattern.MULTILINE)
            val matcher: Matcher = pattern.matcher(urlSender)
            if (matcher.find()) return true
        }
        return false
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        return !isUrlPlayer(url)
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        val url = request?.url.toString()

        val isAd: Boolean
        if (!loadedUrls.containsKey(url)) {
            isAd = AdBlocker.isAd(url)
            loadedUrls.containsValue(isAd)
        } else {
            isAd = loadedUrls[url] == true
        }
        return if (isAd) AdBlocker.createEmptyResource()
        else super.shouldInterceptRequest(view, request)
    }
}