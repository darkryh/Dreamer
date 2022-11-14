package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server


class Uptobox(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.Uptobox
    }
}