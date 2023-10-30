package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.EmbedServer
import com.ead.project.dreamer.data.models.Player

class Mega(embeddedUrl:String) : EmbedServer(embeddedUrl,Player.Mega) {

    override fun setupEmbeddedUrl(embeddedUrl: String?): String {
        return fixRedirectUrl(embeddedUrl?:return url)
    }

    override fun checkIfVideoIsAvailable(): Boolean {
        return !super.checkIfVideoIsAvailable()
    }

    private fun fixRedirectUrl(string: String) : String {
        if (string.contains("/file"))
            return string.replace("/file","/embed#")

        if (string.contains("/#"))
            return string.replace("/#","/embed#")

        return string
    }
}