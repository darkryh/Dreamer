package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.*
import org.json.JSONObject

class Fembed (embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.Fembed
    }

    override fun onExtract() {
        try {
            var request: Request =  Request.Builder().url(url).build()

            var response = OkHttpClient()
                .newCall(request)
                .execute()
            val host = response.request.url.host
            val videoId = PatternManager.singleMatch(
                url,
                "([vf])([/=])(.+)([/&])?",
                3
            ).toString().replace("[&/]", "")

            request = Request.Builder().url("https://$host/api/source/$videoId")
                .post(FormBody.Builder().build())
                .build()

            response = OkHttpClient().newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string().toString()
                val source = JSONObject(body)
                check(source.getBoolean("success")) {  "Request was not succeeded" }
                val array = source.getJSONArray("data")
                for (i in 0 until array.length()) {
                    val `object` = array.getJSONObject(i)
                    val name = `object`.getString("label")
                    url = `object`.getString("file")
                    addVideo(VideoModel(name, url))
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

}