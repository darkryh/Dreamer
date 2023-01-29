package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Voe(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.Voe
    }

    override fun onExtract() {
        val response = OkHttpClient()
            .newCall(Request.Builder().url(url).build())
            .execute()
        url = PatternManager.singleMatch(
            response.body?.string().toString(),
            "\"hls\": \"(.*?)\"")?.replace(",","")

        if (url != null) addVideo(VideoModel("Default",url))
        if(connectionIsNotAvailable()) removeVideos()
        else breakOperation()
    }

}