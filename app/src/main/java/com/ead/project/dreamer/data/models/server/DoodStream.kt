package com.ead.project.dreamer.data.models.server

import android.util.Log
import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.MalformedURLException
import java.net.URL
import java.security.SecureRandom


class DoodStream(embeddedUrl:String) : Server(embeddedUrl) {

    var urlt: String? = null
    var token: String? = null
    var isLong = false

    override fun onPreExtract() {
        super.onPreExtract()
        player = Player.DoodStream
    }

    override fun onExtract() {
        try {
            return //TODO
            if(url.contains("LONG")){
                isLong = true;
                url = url.replace("LONG", "");
            } else {
                isLong = false;
                url = url.replace("/d/", "/e/");
            }
            var request: Request =  Request.Builder().url(url).build()

            var response = OkHttpClient()
                .newCall(request)
                .execute()

            var responseBody = response.body?.string().toString()

            val urlFix = PatternManager.singleMatch(
                responseBody,
                "dsplayer\\.hotkeys[^']+'([^']+).+?function"
            ).toString()

            urlt = "https://" + getHost(url) + urlFix
            Log.d("testing", "onExtract: $urlt")

            token = PatternManager.singleMatch(
                responseBody,
                "makePlay.+?return[^?]+([^\"]+)"
            ).toString()

            Log.d("testing", "onExtract: $token")

            request  =  Request.Builder()
                .url(urlt!!)
                .header("Referer",url)
                .build()

            response = OkHttpClient()
                .newCall(request)
                .execute()

            responseBody = response.body?.string().toString()

            val test: String = responseBody + randomStr(10) + token + System.currentTimeMillis() / 1000L

            Log.d("testing", "onExtract: $test")
        } catch (e : Exception) { e.printStackTrace() }
    }

    @Throws(MalformedURLException::class)
    private fun getHost(uri: String): String? {
        val url = URL(uri)
        return url.host
    }

    private fun randomStr(len: Int): String {
        val AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val rnd = SecureRandom()
        val sb = StringBuilder(len)
        for (i in 0 until len) sb.append(AB[rnd.nextInt(AB.length)])
        return sb.toString()
    }
}