package com.ead.project.dreamer.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest


@SuppressLint("SetJavaScriptEnabled")
class DreamerWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?= null,
    defStyle : Int=0,
    defStylerRes: Int=0) : WebView(context,attrs,defStyle,defStylerRes) {

    init {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.userAgentString = DreamerRequest.userAgent()
        webViewClient = DreamerBlockClient()
    }

    var isLoading = true

    companion object {
        const val BLANK_BROWSER = "about:blank"
        const val TIMEOUT_MS = 10000L

        fun getServerScript() = DataStore.readString(Constants.PREFERENCE_SERVER_SCRIPT)

        fun setServerScript(value : String) = DataStore.writeString(Constants.PREFERENCE_SERVER_SCRIPT,value)

    }

}