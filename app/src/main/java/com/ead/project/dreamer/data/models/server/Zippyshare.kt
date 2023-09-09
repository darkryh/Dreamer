package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server


class Zippyshare(embeddedUrl: String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.Zippyshare
    }

    override fun onExtract() {
        return //Rip ZippyShare
    }

}