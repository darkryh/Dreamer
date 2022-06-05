package com.ead.project.dreamer.data.database.model.server

import android.util.Log
import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import org.json.JSONArray
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.ArrayList

class Videobin (var url : String) : Server() {

    init {
        player = Player.Videobin
        patternReference()
        linkProcess()
    }

    override fun patternReference() {
        super.patternReference()
        try {
            url = PatternManager.variableReference(Jsoup.connect(url)
                .userAgent(DreamerRequest.userAgent())
                .method(Connection.Method.GET).execute().body(),"sources:(.*),")!!
                .trim { it <= ' ' }
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun linkProcess() {
        super.linkProcess()

        try {
            val array = JSONArray(url)
            val list: MutableList<String> = ArrayList()

            for (i in 0 until array.length()) {
                val src = array.getString(i)
                if (!src.endsWith(".m3u8")) {
                    list.add(src)
                }
            }

            for (i in list.indices) {
                videoList.add(VideoModel(quality(list.size, i),list[i]))
            }
            breakOperation()
        }
        catch (e : Exception) {
            Log.e("error", "linkProcess: $e")
        }


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