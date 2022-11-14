package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class Onefichier (embeddedUrl:String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.Onefichier
    }

    override fun onExtract() {
        try {
            url = fixUrl(url)
            val request: Request = Request.Builder()
                .url(url)
                .header("Cookie", "SID=")
                .header("Authorization", "Basic ${getToken()}")
                .build()

            val client: OkHttpClient.Builder = OkHttpClient.Builder()
                .addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val networkRequest = chain.request()
                    val response = chain.proceed(networkRequest)
                    val redirect = response.header("Location")
                    if (redirect != null) url = redirect
                    response
                })
            val response: Response = client.build().newCall(request).execute()
            if (response.isSuccessful) addDefaultVideo()
            breakOperation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fixUrl(url: String): String = "$url?=&auth=1"
    private fun getToken() = "YWxleF9ibGFjay14Y0Bob3RtYWlsLmNvbTpLaXJhMTAwOA=="
}