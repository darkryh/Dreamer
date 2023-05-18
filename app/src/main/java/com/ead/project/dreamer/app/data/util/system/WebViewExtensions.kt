package com.ead.project.dreamer.app.data.util.system

import android.webkit.WebView
import com.ead.project.dreamer.app.data.util.HttpUtil


fun WebView.load(url : String) {
    clearData()
    loadUrl(url)
}

fun WebView.clearData() {
    this.clearHistory()
    this.clearCache(true)
}

fun WebView.onDestroy() {
    this.loadUrl(HttpUtil.BLANK_BROWSER)
    this.onPause()
    this.removeAllViews()
    this.destroy()
}