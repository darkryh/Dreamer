package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import org.jsoup.Jsoup

class Mp4Upload (var url : String) : Server() {

    init {
        player = Player.Mp4Upload
        isDirect = false
        if (!fileDeleted()) videoList.add(VideoModel("Default",url))
    }

    private fun fileDeleted() = Jsoup.connect(url).get().body().text() == "File was deleted"
}