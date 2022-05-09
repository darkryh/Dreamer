package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException

class Fembed (var url :String) : Server() {

    init {
        patternReference()
        linkProcess()
    }

    override fun patternReference() {
        super.patternReference()
        val response = Jsoup.connect(url).followRedirects(true).execute()
        domain = response.url().toString().substringBefore("/v/")
        videoId = PatternManager.sliceReference(url)!!
    }

    override fun linkProcess() {
        super.linkProcess()

        try {
            val request = Request.Builder()
                .url(domain + "/api/source/${videoId}")
                .post(
                    FormBody.Builder()
                        .add("client_id","369484")
                        .add("client_secret","33143aad190dda88").build())
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            val source = JSONObject(response.body!!.string())
                            check(source.getBoolean("success")) {  "Request was not succeeded" }
                            val objectData = source.getJSONArray("data")
                            for (pos in 0 until  objectData.length()) {
                                val name: String = (objectData[pos] as JSONObject).getString("label")
                                url = (objectData[pos] as JSONObject).getString("file")
                                videoList.add(VideoModel(name,url))
                            }
                            if(!connectionAvailable())
                                videoList.clear()
                        }
                    }
                    catch (e : Exception) { e.printStackTrace() }
                }
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) { e.printStackTrace() }
    }
}