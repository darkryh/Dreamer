package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import org.jsoup.Connection
import org.jsoup.Jsoup

class Senvid(var url : String) : Server() {

    init {
        player = Player.Senvid
        patternReference()
        linkProcess()
    }

    override fun patternReference() {
        super.patternReference()
        try {
            url = PatternManager.variableReference(Jsoup.connect(url)
                .userAgent(DreamerRequest.userAgent())
                .method(Connection.Method.GET).execute().body()
                ,"<source src=\"(.*?)\"").toString()
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun linkProcess() {
        super.linkProcess()
        videoList.add(VideoModel("Default",url))
    }

}