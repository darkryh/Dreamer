package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoModel
import org.jsoup.Jsoup

class Uqload (embeddedUrl:String) : Server(embeddedUrl) {

    override fun onExtract() {
        if (isDownloading) return
        player = Player.Uqload
        isDirect = false
        if (!fileDeleted()) addVideo(VideoModel("Default",url))
    }

    private fun fileDeleted() : Boolean = try {
        Jsoup.connect(url).get().body().text() == "File was deleted"
    } catch (e : Exception) { true }
}