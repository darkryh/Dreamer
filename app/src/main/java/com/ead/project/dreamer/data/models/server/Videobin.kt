package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import org.json.JSONArray
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.ArrayList

class Videobin (embeddedUrl:String) : Server(embeddedUrl, Player.Videobin) {

    override fun onExtract() {
        try {
            url = PatternManager.singleMatch(Jsoup.connect(url)
                .userAgent(DreamerRequest.userAgent())
                .method(Connection.Method.GET).execute().body(),"sources:(.*),")

            val array = JSONArray(url)
            val list: MutableList<String> = ArrayList()

            for (i in 0 until array.length()) {
                val src = array.getString(i)
                if (!isDownloading)
                    if (src.endsWith(".m3u8")) list.add(src)
                else
                    if (!src.endsWith(".m3u8")) list.add(src)
            }
            for (i in list.indices) addVideo(VideoModel(quality(list.size, i),list[i]))
            endProcessing()
        }
        catch (e : Exception) { e.printStackTrace() }
    }

    private fun quality(size: Int, index: Int): String {
        val quality: MutableList<String> = ArrayList()
        when (size) {
            1 -> quality.add("480p")
            2 -> {
                quality.add("720p")
                quality.add("480p")
            }
            3 -> {
                quality.add("1080p")
                quality.add("720p")
                quality.add("480p")
            }
            4 -> {
                quality.add("Higher")
                quality.add("1080p")
                quality.add("720p")
                quality.add("480p")
            }
        }
        return quality[index]
    }
}