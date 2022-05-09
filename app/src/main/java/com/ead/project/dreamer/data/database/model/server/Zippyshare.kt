package com.ead.project.dreamer.data.database.model.server


import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import net.objecthunter.exp4j.ExpressionBuilder
import org.jsoup.Jsoup
import kotlin.math.roundToInt


class Zippyshare (var url : String) : Server() {

    init {
        linkProcess()
    }

    override fun linkProcess() {
        super.linkProcess()
        try {
            val source = Jsoup.connect(url).get()

            val raw = source.html()
                .substringAfter("document.getElementById('dlbutton').href = ${Constants.QUOTATION}")
                .substringBefore("${Constants.QUOTATION};")

            val operation = raw.substringAfter(Constants.QUOTATION).substringBefore(Constants.QUOTATION)
                .removePrefix(" + ").removeSuffix(" + ")
            val resultOperation = ExpressionBuilder(operation).build().evaluate().roundToInt()

            val fistPart = raw.substringBefore("${Constants.QUOTATION} + (")
            val secondPart = raw.substringAfter(") + ${Constants.QUOTATION}")

            val result = fistPart + resultOperation + secondPart

            videoList.add(VideoModel("Default", url.substringBefore("/v/").plus(result)))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}