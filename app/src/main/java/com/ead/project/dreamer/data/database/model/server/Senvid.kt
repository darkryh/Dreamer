package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Senvid(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        super.onPreExtract()
        player = Player.Senvid
    }

    override fun onExtract() {
        super.onExtract()
        if (url.contains("repro.monoschinos2.com")) url = url.substringAfter("url=")
        val response = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
        url = PatternManager.singleMatch(response.body!!.string(),
            "<source src=\"(.*?)\"")
        if (url != null) videoList.add(VideoModel("Default",url))
    }

}