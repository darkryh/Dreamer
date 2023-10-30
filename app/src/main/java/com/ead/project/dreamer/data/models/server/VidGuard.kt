package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.EmbedServer
import com.ead.project.dreamer.data.models.Player

class VidGuard(embeddedUrl:String) : EmbedServer(embeddedUrl,Player.VidGuard) {

    override fun checkIfVideoIsAvailable(): Boolean {
        return !super.checkIfVideoIsAvailable()
    }
}