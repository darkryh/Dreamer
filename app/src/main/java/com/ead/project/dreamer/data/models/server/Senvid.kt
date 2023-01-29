package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Senvid(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.Senvid
    }

    override fun onExtract() {
        if (url.contains("repro.monoschinos2.com")) url = url.substringAfter("url=")
        val response = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
        url = PatternManager.singleMatch(response.body?.string().toString(),
            "<source src=\"(.*?)\"")
        if (url != null) addVideo(VideoModel("Default",url))
    }

}