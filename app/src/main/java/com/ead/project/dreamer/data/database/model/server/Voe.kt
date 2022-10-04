package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Voe(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        super.onPreExtract()
        player = Player.Voe
    }

    override fun onExtract() {
        super.onExtract()
        val response = OkHttpClient()
            .newCall(Request.Builder().url(url).build())
            .execute()
        url = PatternManager.singleMatch(
            response.body!!.string(),
            "\"hls\": \"(.*?)\"")?.replace(",","")
        if (url != null) videoList.add(VideoModel("Default",url))
        if(connectionIsNotAvailable()) removeVideos()
        else breakOperation()
    }

}