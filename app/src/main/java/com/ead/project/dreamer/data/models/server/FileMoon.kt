package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.EmbedServer
import com.ead.project.dreamer.data.models.Player

class FileMoon(embeddedUrl:String) : EmbedServer(embeddedUrl,Player.FileMoon) {

    override fun checkIfVideoIsAvailable(): Boolean {
        return !super.checkIfVideoIsAvailable()
    }
}