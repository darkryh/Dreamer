package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class Onefichier (var url :String) : Server() {

    private var directLink = "null"
    private var token = "token"

    init {
        linkProcess()
    }

    override fun linkProcess() {
        super.linkProcess()
        try {
            val request = Request.Builder()
                .url(finalLink())
                .header("Cookie","SID=")
                .header("Authorization", "Basic $token")
                .build()

            val client = OkHttpClient.Builder()
                .addNetworkInterceptor(Interceptor { chain ->
                    val networkRequest = chain.request()
                    val response = chain.proceed(networkRequest)
                    val redirect = response.header("Location")
                    if (redirect != null) {
                        directLink = redirect
                    }
                    response
                })

            try {
                val requestData = client.build().newCall(request).execute()
                if (requestData.isSuccessful) {
                    if (directLink != "null")
                        videoList.add(VideoModel("Default",directLink))
                }
            } catch (e : IOException) {
                e.printStackTrace()
            }
        } catch (e : IOException) {
            e.printStackTrace()
        }
    }

    private fun finalLink() = "$url?=&auth=1"
}