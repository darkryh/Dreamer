package com.ead.project.dreamer.app.data.server.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest

@SuppressLint("SetJavaScriptEnabled")
open class ServerWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?= null,
    defStyle : Int=0,
    defStylerRes: Int=0) : WebView(context,attrs,defStyle,defStylerRes) {

    init {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.userAgentString = DreamerRequest.userAgent()
    }
}