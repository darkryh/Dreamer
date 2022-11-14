package com.ead.project.dreamer.data.models.server

import com.ead.project.dreamer.data.models.Player
import com.ead.project.dreamer.data.models.Server
import com.ead.project.dreamer.data.utils.PatternManager
import net.objecthunter.exp4j.ExpressionBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.math.roundToInt


class Zippyshare (embeddedUrl: String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        player = Player.Zippyshare
    }

    override fun onExtract() {
        try {
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()
            val host = response.request.url.host
            val data: String = PatternManager.singleMatch(
                response.body?.string().toString(),
                    "(href = \"/d/)(.+)\"",
                    0
                ).toString().replace("href = ", "").replace("\"", "")
            val stringList = data.split(" ").toTypedArray()
            val operation = (ExpressionBuilder(
                stringList[2] + stringList[3] + stringList[4] +
                        stringList[5] + stringList[6] + stringList[7] +
                        stringList[8]
            ).build().evaluate().roundToInt())
            url = "https://" + host + stringList[0] + operation + stringList[10]
            addDefaultVideo()
            breakOperation()
        } catch (e: Exception) { e.printStackTrace() }
    }

}