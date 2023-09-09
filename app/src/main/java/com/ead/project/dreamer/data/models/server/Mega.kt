package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoModel

class Mega(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onExtract() {
        if (isDownloading) return
        player = Player.Mega
        isDirect = false
        url = fixUrl(url)
        addVideo(VideoModel("Default",url))
    }

    private fun fixUrl(string: String) : String {
        if (string.contains("/file"))
            return string.replace("/file","/embed#")

        if (string.contains("/#"))
            return string.replace("/#","/embed#")

        return string
    }
}