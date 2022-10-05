package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel

class Mega (embeddedUrl:String) : Server(embeddedUrl) {

    override fun onExtract() {
        super.onExtract()
        if (isDownloading) return
        player = Player.Mega
        isDirect = false
        url = fixUrl(url)
        videoList.add(VideoModel("Default",url))
    }

    private fun fixUrl(string: String) = string
        .replace("/file","/embed#")
        .replace("/#","/embed#")
}