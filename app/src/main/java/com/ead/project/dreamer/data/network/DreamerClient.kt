package com.ead.project.dreamer.data.network

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.ThreadUtil

open class DreamerClient : WebViewClient() {

    var timeout = true
    var timesLoaded = 0
    fun run(task: () -> Unit) = ThreadUtil.runInMs(task, 10000)

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onPageInit(view, url, favicon)
        timeout = true
        run { if (timeout && url != Constants.BLANK_BROWSER) onTimeout(view, url, favicon) }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        timeout = false
        timesLoaded++
        if (url != Constants.BLANK_BROWSER) onPageLoaded(view, url)
    }

    open fun onPageInit(view: WebView?, url: String?, favicon: Bitmap?) {}

    open fun onPageLoaded(view: WebView?, url: String?) {}

    open fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {}

}