package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server


class MegaUp(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.MegaUp
    }
}