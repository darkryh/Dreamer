package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Bayfiles(embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        super.onPreExtract()
        player = Player.Bayfiles
    }

    override fun onExtract() {
        super.onExtract()
        try {
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()

            val totalData: List<String> = PatternManager.multipleMatches(
                response.body!!.string(),
                "https?:\\/\\/(cdn-[0123456789][0123456789][0123456789]).(bayfiles\\.com\\/.+)",
                0
            )
            if (totalData.size > 1)
                for (i in 0 until totalData.size / 2) {
                    url = fixDownloadLinks(totalData[i])
                    videoList.add(VideoModel(quality(i),url))
                }
            else {
                url = fixDownloadLinks(totalData[0])
                videoList.add(VideoModel("default",url))
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun fixDownloadLinks(string: String): String {
        return string.trim { it <= ' ' }
            .replace("\"", "")
            .replace(" ", "")
            .replace("img", "")
            .replace(">", "")
            .replace("<", "")
    }

    private fun quality(index : Int) :String {
        when (index) {
            0 -> return "720p"
            1 -> return "480p"
        }
        return "Default"
    }
}