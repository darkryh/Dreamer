package com.ead.project.dreamer.data.models.server

import android.content.Context
import com.ead.lib.moongetter.core.system.extensions.await
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class Puj (context: Context, url : String) : Server(context, url) {

    override suspend fun onExtract() {
        var response = OkHttpClient()
            .newCall(Request.Builder().url(url).build())
            .execute()

        url = PatternManager.singleMatch(response.body?.string().toString(), "file: '(.+)'") ?: return

        val request: Request =  Request.Builder().url(url).build()

        response = OkHttpClient()
            .newCall(request)
            .await()

        if (!response.isSuccessful) return

        addDefault()
    }
}