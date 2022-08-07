package com.ead.project.dreamer.data.database.model.server

import android.util.Log
import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import org.jsoup.Connection
import org.jsoup.Jsoup

class Voe(var url : String) : Server() {

    init {
        player = Player.Voe
        patternReference()
        linkProcess()
    }

    override fun patternReference() {
        super.patternReference()
        try {
            url = PatternManager.variableReference(Jsoup.connect(url)
                .userAgent(DreamerRequest.userAgent())
                .method(Connection.Method.GET).execute().body()
                ,"\"hls\": \"(.*?)\"")?.replace(",","")?: "null"
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun linkProcess() {
        super.linkProcess()
        if (url != "null") videoList.add(VideoModel("Default",url))
        if(!connectionAvailable()) videoList.clear()
        else breakOperation()
    }

}