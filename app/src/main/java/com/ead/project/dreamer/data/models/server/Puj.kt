package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Puj (embeddedUrl: String) : Server(embeddedUrl,Player.Puj) {

    override fun onExtract() {
        try {
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()
            url = PatternManager.singleMatch(
                response.body?.string().toString(),
                "file: '(.+)'")
            addVideo(VideoModel("Default",url))
            if (isConnectionNotValidated) removeVideos()
            else endProcessing()
        } catch (e: Exception) { e.printStackTrace() }
    }
}