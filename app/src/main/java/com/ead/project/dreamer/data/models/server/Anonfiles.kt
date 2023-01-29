package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Anonfiles(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.Anonfile
    }

    override fun onExtract() {
        try {
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()

            url = PatternManager.singleMatch(
                response.body?.string().toString(),
                "https?:\\/\\/(cdn-[0123456789][0123456789][0123456789]).(anonfiles\\.com\\/.+)",
                0
            )
            if (url != null) addDefaultVideo()
        } catch (e :Exception) { e.printStackTrace()}
    }
}