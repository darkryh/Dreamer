package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server

class PixelDrain(embeddedUrl: String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.PixelDrain
    }

    override fun onExtract() {
        try {

            val regexPattern = Regex("""/u/([a-zA-Z0-9]+)""")
            val matchResult = regexPattern.find(url)
            val id = matchResult?.groupValues?.get(1)

            url = "https://pixeldrain.com/api/file/$id?download"

            addDefaultVideo()

        } catch (e: Exception) { e.printStackTrace() }
    }
}