package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.EmbedServer
import com.ead.project.dreamer.data.models.Player

class MixDrop(embeddedUrl:String) : EmbedServer(embeddedUrl,Player.MixDrop) {

    override fun checkIfVideoIsAvailable(): Boolean {
        return !super.checkIfVideoIsAvailable()
    }
}