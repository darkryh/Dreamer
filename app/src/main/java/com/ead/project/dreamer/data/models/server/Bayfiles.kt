package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.app.data.util.system.delete
import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Bayfiles(embeddedUrl:String) : Server(embeddedUrl,Player.Bayfiles) {

    override fun onExtract() {
        try {
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()

            val totalData: List<String> = PatternManager.multipleMatches(
                response.body?.string().toString(),
                "https?:\\/\\/(cdn-[0123456789][0123456789][0123456789]).(bayfiles\\.com\\/.+)",
                0
            )
            if (totalData.size > 1)
                for (i in 0 until totalData.size / 2) {
                    url = fixDownloadLinks(totalData[i])
                    addVideo(VideoModel(quality(i),url))
                }
            else {
                url = fixDownloadLinks(totalData[0])
                addDefaultVideo()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun fixDownloadLinks(string: String): String {
        return string.trim { it <= ' ' }
            .delete("\"")
            .delete(" ")
            .delete("img")
            .delete(">")
            .delete("<")
    }

    private fun quality(index : Int) :String {
        when (index) {
            0 -> return "720p"
            1 -> return "480p"
        }
        return "Default"
    }
}