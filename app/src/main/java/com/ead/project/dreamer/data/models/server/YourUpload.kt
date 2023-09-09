package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.EmbedServer
import com.ead.project.dreamer.data.models.Player

class YourUpload(embeddedUrl:String) : EmbedServer(embeddedUrl,Player.YourUpload) {

    override fun checkIfVideoIsAvailable(): Boolean {
        return !super.checkIfVideoIsAvailable()
    }
}