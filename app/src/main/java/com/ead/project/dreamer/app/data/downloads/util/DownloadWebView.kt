package com.ead.project.dreamer.app.data.downloads.util

import android.content.Context
import android.util.AttributeSet
import com.ead.project.dreamer.app.data.server.util.ServerWebView

class DownloadWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?= null,
    defStyle : Int=0,
    defStylerRes: Int=0) : ServerWebView(context,attrs,defStyle,defStylerRes)
