package com.ead.project.dreamer.data.models.server

import android.content.Context
import com.ead.project.dreamer.data.models.EmbedServer


class DoodStream(context: Context, url : String) : EmbedServer(context, url) {
    override fun isAvailable(): Boolean { return !super.isAvailable() }

    //TESTING

    /*private var urlTemp: String? = null
    private var rawToken: String? = null
    private var host : String? = null

    override fun onExtract() {
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

            val requestUrl = url.substringBefore("/e/").plus("/dood")

            Log.d("test", "urlContainer: $urlTemp")

            rawToken = PatternManager.singleMatch(
                responseBody,
                "makePlay.+?return[^?]+([^\"]+)"
            ).toString()

            val hash = urlTemp?.
            substringAfter("pass_md5/")?.
            substringBefore("/")

            Log.d("test", "hash: $hash")

            val token = rawToken?.
            substringAfter("?token=")?.
            substringBefore("&expiry=")

            Log.d("test", "token: $token")


            request = Request.Builder()
                .url(urlTemp?:return)
                .header("Referer",url)
                .build()

            response = OkHttpClient()
                .newCall(request)
                .execute()

            responseBody = response.body?.string().toString()

            url = responseBody + randomStr() + rawToken + System.currentTimeMillis() / 1000L

            Log.d("test", "onExtract: $url")

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

            Log.d("test", "onExtract: ${response.code}")

        } catch (e : Exception) {
            Log.d("test", "error: ${e.message}") }
    }

    private fun randomStr(): String {
        val length = 10

        val abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val rnd = SecureRandom()
        val stringBuilder = StringBuilder(length)

        for (i in 0 until length)
            stringBuilder.append(abc[rnd.nextInt(abc.length)])

        return stringBuilder.toString()
    }*/
}