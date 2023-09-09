package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.EmbedServer
import com.ead.project.dreamer.data.models.Player

class StreamWish(embeddedUrl:String) : EmbedServer(embeddedUrl,Player.StreamWish) {

    override fun checkIfVideoIsAvailable(): Boolean {
        return !super.checkIfVideoIsAvailable()
    }
}