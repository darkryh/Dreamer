package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import org.jsoup.Jsoup

class Puj (var url : String) : Server() {

    init {
        linkProcess()
    }

    override fun linkProcess() {
        super.linkProcess()
        try {

            val source = Jsoup.connect(url).get()

            val script = source.select("script").last()!!.html()
            val reDir = script.lines()[12]
            url = reDir.trim().removePrefix("file: '").removeSuffix("',")

            videoList.add(VideoModel("Default",url))
        } catch (e: Exception) { e.printStackTrace() }
    }
}