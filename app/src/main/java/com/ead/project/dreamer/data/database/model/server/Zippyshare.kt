package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Player
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.utils.PatternManager
import net.objecthunter.exp4j.ExpressionBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.math.roundToInt


class Zippyshare (embeddedUrl: String) : Server(embeddedUrl) {

    override fun onPreExtract() {
        super.onPreExtract()
        player = Player.Zippyshare
    }

    override fun onExtract() {
        super.onExtract()
        try {
            val response = OkHttpClient()
                .newCall(Request.Builder().url(url).build())
                .execute()
            val host = response.request.url.host
            val data: String = PatternManager.singleMatch(
                response.body!!.string(),
                    "(href = \"/d/)(.+)\"",
                    0
                )!!.replace("href = ", "").replace("\"", "")
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