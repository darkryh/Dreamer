package com.ead.project.dreamer.data.models.server


import com.ead.project.dreamer.data.models.EmbedServer
import com.ead.project.dreamer.data.models.Player
import org.jsoup.Jsoup

class Mp4Upload(embeddedUrl:String) : EmbedServer(embeddedUrl,Player.Mp4Upload) {

    override fun checkIfVideoIsAvailable(): Boolean {
        return !(try {
            Jsoup.connect(url).get()
                .body()
                .text() == "File was deleted"
        }
        catch (ex : Exception) {
            true
        })
    }

}