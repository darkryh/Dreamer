package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server

class DoodStream(embeddedUrl:String) : Server(embeddedUrl) {

    //private val token = "needsToken"

    //regex dsplayer\.hotkeys[^']+'([^']+).+?function
    //regex makePlay.+?return[^?]+([^"]+)

    override fun onPreExtract() {
        player = Player.DoodStream
    }
}