package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class StreamWish(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onExtract() {
        try {
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()

            url = PatternManager.singleMatch(
                response.body?.string().toString(),
                """file:"(https://[^"]+)"""",
                1
            )
            if (url != null) addDefaultVideo()
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }
}