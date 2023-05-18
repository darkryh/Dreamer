package com.ead.project.dreamer.app.data.util

import android.webkit.URLUtil

object UrlUtil {

    fun isValid(url : String) : Boolean = URLUtil.isValidUrl(url)

}