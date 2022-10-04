package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Puj (embeddedUrl: String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        super.onPreExtract()
        player = Player.Puj
    }

    override fun onExtract() {
        super.onExtract()
        try {
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()
            url = PatternManager.singleMatch(
                response.body!!.string(),
                "file: '(.+)'")
            videoList.add(VideoModel("Default",url))
            if (connectionIsNotAvailable()) removeVideos()
            else breakOperation()
        } catch (e: Exception) { e.printStackTrace() }
    }
}