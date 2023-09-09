package com.ead.project.dreamer.data.models.server

import android.util.Log
import com.ead.project.dreamer.data.models.EmbedServer
import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL
import java.security.SecureRandom


class DoodStream(embeddedUrl:String) : EmbedServer(embeddedUrl,Player.DoodStream) {

    private var urlTemp: String? = null
    private var token: String? = null
    private var host : String? = null

    @Suppress("UNREACHABLE_CODE")
    override fun onExtract() {
        return

        try {

            val previousHost = URL(url).host

            var request: Request =  Request.Builder().url(url).build()

            var response = OkHttpClient()
                .newCall(request)
                .execute()

            var responseBody = response.body?.string().toString()


            host = response.request.url.host
            url = url.replace(previousHost,host?:return)

            val keysCode = PatternManager.singleMatch(
                responseBody,
                "dsplayer\\.hotkeys[^']+'([^']+).+?function"
            ).toString()

            urlTemp = "https://$host$keysCode"

            token = PatternManager.singleMatch(
                responseBody,
                "makePlay.+?return[^?]+([^\"]+)"
            ).toString()


            request = Request.Builder()
                .url(urlTemp?:return)
                .header("Referer",url)
                .build()

            response = OkHttpClient()
                .newCall(request)
                .execute()

            responseBody = response.body?.string().toString()

            url = responseBody + randomStr() + token + System.currentTimeMillis() / 1000L

            Log.d("testing", "onExtract: $url")

            request = Request.Builder()
                .url(url)
                .build()



            val client: OkHttpClient.Builder = OkHttpClient.Builder()
                .addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->

                    val networkRequest = chain.request()
                    response = chain.proceed(networkRequest)
                    val redirect = response.header("Location")

                    if (redirect != null) url = redirect
                    response
                })

            response = client.build().newCall(request).execute()

            if (response.code in 301..309) addDefaultVideo()

            Log.d("testing", "onExtract: ${response.code}")

        } catch (e : Exception) {
            Log.d("testing", "onExtract: ${e.message}") }
    }

    override fun checkIfVideoIsAvailable(): Boolean {
        return !super.checkIfVideoIsAvailable()
    }


    private fun randomStr(): String {
        val length = 10

        val abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val rnd = SecureRandom()
        val stringBuilder = StringBuilder(length)

        for (i in 0 until length)
            stringBuilder.append(abc[rnd.nextInt(abc.length)])

        return stringBuilder.toString()
    }
}