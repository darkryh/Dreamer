package com.ead.project.dreamer.data.models.server

import android.content.Context
import com.ead.lib.moongetter.core.system.extensions.await
import com.ead.lib.moongetter.models.Server
import com.ead.lib.moongetter.models.exceptions.InvalidServerException
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.utils.PatternManager
import okhttp3.OkHttpClient
import okhttp3.Request

class GoodStream(context: Context, url : String) : Server(context, url) {

    override suspend fun onExtract() {
        val response = OkHttpClient()
            .newCall(Request.Builder().url(url).build())
            .await()

        if (!response.isSuccessful) throw InvalidServerException(context.getString(R.string.server_warning_error))

        val body = response.body?.string().toString()



        url = PatternManager.singleMatch(
            string = body,
            regex = """https://s\d+\.goodstream\.uno/\S+\?download_token=\S+(?=")""",
            groupIndex = 0
        ) ?: throw InvalidServerException(context.getString(R.string.server_warning_error))

        addDefault()
    }
}