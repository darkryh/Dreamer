package com.ead.project.dreamer.data.models.server


import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoModel
import org.jsoup.Jsoup

class Mp4Upload (embeddedUrl:String) : Server(embeddedUrl) {

    override fun onExtract() {
        if (isDownloading) return
        player = Player.Mp4Upload
        isDirect = false
        if (!fileDeleted()) addVideo(VideoModel("Default",url))
    }

    private fun fileDeleted(): Boolean =
        try { Jsoup.connect(url).get().body().text() == "File was deleted" }
        catch (ex : Exception) { true }
}