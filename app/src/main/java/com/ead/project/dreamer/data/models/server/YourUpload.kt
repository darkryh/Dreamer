package com.ead.project.dreamer.data.models.server

import android.content.Context
import com.ead.project.dreamer.data.models.EmbedServer
import org.jsoup.Jsoup

class YourUpload(context: Context, url : String) : EmbedServer(context, url) {
    override fun isAvailable(): Boolean {
        return !(try {
            Jsoup.connect(url).get()
                .title() == "Content Invalid"
        }
        catch (ex : Exception) {
            true
        })
    }
}