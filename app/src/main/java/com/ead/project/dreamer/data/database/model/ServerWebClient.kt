package com.ead.project.dreamer.data.database.model

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.ead.project.dreamer.data.network.DreamerClient
import com.ead.project.dreamer.data.network.DreamerWebView

open class ServerWebClient(var webView: DreamerWebView?) : DreamerClient() {

    override fun onTimeout(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onTimeout(view, url, favicon)
        webView?.isLoading = false
    }
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        webView?.isLoading = false
    }
}