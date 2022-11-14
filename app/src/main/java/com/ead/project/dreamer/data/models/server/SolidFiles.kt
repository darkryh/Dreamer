package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class SolidFiles(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.SolidFiles
    }

    override fun onExtract() {
        try {
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()
            url = PatternManager.singleMatch(
                response.body?.string().toString(),
                "downloadUrl\":\"(.*?)\"")

            if (url != null) {
                addDefaultVideo()
                breakOperation()
            }
        } catch (e :Exception) { e.printStackTrace() }
    }

}