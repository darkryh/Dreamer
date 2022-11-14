package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server

class YourUpload(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onExtract() {
        player = Player.YourUpload
    }
}