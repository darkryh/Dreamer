package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import org.apache.commons.text.StringEscapeUtils
import org.json.JSONObject
import org.jsoup.Jsoup

class Okru(var url:String) : Server() {

    init {
        player = Player.Okru
        patternReference()
        linkProcess()
    }

    override fun patternReference() {
        super.patternReference()
        try {
            url = url.replace("//","https://")
            url = PatternManager.variableReference(Jsoup.connect(url)
                .userAgent(DreamerRequest.getSpecificUserAgent(0))
                .ignoreContentType(true)
                .execute().body(),"data-options=\"(.*?)\"")!!
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun linkProcess() {
        super.linkProcess()
        try {

            url = StringEscapeUtils.unescapeHtml4(url)

            val json = JSONObject(url)
                .getJSONObject("flashvars")
                .getString("metadata")

            val objectData = JSONObject(json).getJSONArray("videos")
            var video: VideoModel

            for (pos in 0 until objectData.length()) {
                val url: String = objectData.getJSONObject(pos).getString("url")
                video = when (objectData.getJSONObject(pos).getString("name")) {
                    "mobile" -> VideoModel("144p", url)
                    "lowest" -> VideoModel("240p", url)
                    "low"    -> VideoModel("360p", url)
                    "sd"     -> VideoModel("480p", url)
                    "hd"     -> VideoModel("720p", url)
                    "full"   -> VideoModel("1080p", url)
                    "quad"   -> VideoModel("2000p", url)
                    "ultra"  -> VideoModel("4000p", url)
                    else     -> VideoModel("Default", url)
                }
                videoList.add(video)
            }
            breakOperation()
        } catch (e: Exception) { e.printStackTrace() }
    }
}