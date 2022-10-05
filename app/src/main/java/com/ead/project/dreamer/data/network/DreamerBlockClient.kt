package com.ead.project.dreamer.data.network

import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ead.project.dreamer.data.utils.ThreadUtil
import java.util.regex.Matcher
import java.util.regex.Pattern


open class DreamerBlockClient : WebViewClient() {

    @Suppress("unused")
    var timeout = true
    fun run (task: () -> Unit) = ThreadUtil.runInMs(task,10000)

    private val loadedUrls: Map<String, Boolean> = HashMap()
    private var validUrls: List<String> = listOf(
        "https://www\\.monoschinos2\\.com/*",
        "https://www\\.mp4upload\\.com/*",
        "https://www\\.uqload\\.com/*",
        "https://www\\.mega\\.nz/*")

    private fun isValidUrl(url: String): Boolean {
        for (validUrl in validUrls) {
            val pattern: Pattern = Pattern.compile(validUrl, Pattern.MULTILINE)
            val matcher: Matcher = pattern.matcher(url)
            if (matcher.find()) return true
        }
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean = !isValidUrl(url)

    @Deprecated("Deprecated in Java")
    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        val ad: Boolean
        if (!loadedUrls.containsKey(url)) {
            ad = AdBlocker.isAd(url)
            loadedUrls.containsValue(ad)
        } else {
            ad = loadedUrls[url] == true
        }
        return if (ad) AdBlocker.createEmptyResource() else super.shouldInterceptRequest(view, url)
    }
}