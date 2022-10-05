package com.ead.project.dreamer.data.database.model.server


import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import org.jsoup.Jsoup

class Mp4Upload (embeddedUrl:String) : Server(embeddedUrl) {

    override fun onExtract() {
        super.onExtract()
        if (isDownloading) return
        player = Player.Mp4Upload
        isDirect = false
        if (!fileDeleted()) videoList.add(VideoModel("Default",url))
    }

    private fun fileDeleted(): Boolean =
        try { Jsoup.connect(url).get().body().text() == "File was deleted" }
        catch (ex : Exception) { true }
}