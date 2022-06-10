package com.ead.project.dreamer.data.database.model.server

import android.util.Log
import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import org.jsoup.Connection
import org.jsoup.Jsoup

class SolidFiles(var url : String) : Server() {

    init {
        player = Player.SolidFiles
        patternReference()
        linkProcess()
    }

    override fun patternReference() {
        super.patternReference()
        try {
            url = PatternManager.variableReference(Jsoup.connect(url)
                .userAgent(DreamerRequest.getSpecificUserAgent(1))
                .method(Connection.Method.GET).execute().body()
                ,"downloadUrl\":\"(.*?)\"").toString()
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun linkProcess() {
        super.linkProcess()
        try {
            val container = Jsoup.connect(url).get().body().getElementsByClass("reg-text").text()
            if ("Namecheap.com." !in container) {
                videoList.add(VideoModel("Default",url))
                breakOperation()
            }
        } catch (e :Exception) {
            e.printStackTrace()
        }
    }

}