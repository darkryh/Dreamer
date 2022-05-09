package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import org.jsoup.Jsoup

class Bayfiles(var url : String) : Server() {

    init {
        linkProcess()
    }

    override fun linkProcess() {
        super.linkProcess()
        try {
            val source = Jsoup.connect(url).get()

            val container = source
                .getElementsByClass("col-xs-12 col-md-4 text-center")[0].children()

            for (index in container.indices-1) {
                videoList.add(
                    VideoModel(
                        quality(index),
                        container[index].attr("href")
                    ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun quality(index : Int) :String {
        when (index) {
            0 -> return "720p"
            1 -> return "480p"
        }
        return "Default"
    }
}