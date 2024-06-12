package com.ead.project.dreamer.data.models.server

import android.content.Context
import com.ead.project.dreamer.data.models.EmbedServer

class Mega(context: Context, url : String) : EmbedServer(context, url) {

    override var url: String = fixRedirectUrl(url)
    override fun isAvailable(): Boolean { return !super.isAvailable() }

    private fun fixRedirectUrl(string: String) : String {
        if (string.contains("/file"))
            return string.replace("/file","/embed#")

        if (string.contains("/#"))
            return string.replace("/#","/embed#")

        return string
    }
}