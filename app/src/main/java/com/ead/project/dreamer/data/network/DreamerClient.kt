package com.ead.project.dreamer.data.network

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ead.project.dreamer.app.data.util.HttpUtil
import com.ead.project.dreamer.data.utils.Thread

open class DreamerClient : WebViewClient() {

    var timeout = true
    var timesLoaded = 0
    private fun run(task: () -> Unit) = Thread.runInMs(task, 10000)
    fun onUi(task: () -> Unit) = Thread.onUi { task() }

    @Deprecated("OnPageInit option")
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onPageInit(view, url, favicon)
        timeout = true
        run { if (timeout && url != HttpUtil.BLANK_BROWSER) onTimeout(view, url, favicon) }
    }

    @Deprecated("OnPageLoaded option")
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        timeout = false
        timesLoaded++
        if (url != HttpUtil.BLANK_BROWSER) onPageLoaded(view, url)
    }

    open fun onPageInit(view: WebView?, url: String?, favicon: Bitmap?) {}

    open fun onPageLoaded(view: WebView?, url: String?) {}

    open fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {}

}