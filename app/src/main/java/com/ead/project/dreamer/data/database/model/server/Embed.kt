package com.ead.project.dreamer.data.database.model.server

import com.ead.project.dreamer.data.database.model.Server
import com.ead.project.dreamer.data.database.model.VideoModel
import org.jsoup.Jsoup

class Embed(var url : String) : Server() {

    init {
        linkProcess()
    }

    override fun linkProcess() {
        super.linkProcess()

        try {

            val source = Jsoup.connect(url.replace("/e/","/d/")).get()

            val table = source.select("table")
            val body = table.first()!!.select("tbody")
            var container = body.select("tr")[1]
                .select("td").first()!!
                .select("a")
            val protectedLink = container.attr("onclick")
            val data = protectedLink.removePrefix("download_video(")
                .removeSuffix(")")
            val content = data.split("','")
            val id = content[0].removeRange(0,1)
            val mode = content[1]
            val hash = content[2].removeRange(content[2].length-1,content[2].length)
            url = "https://embedsb.com/dl?op=download_orig" +
                    "&id=" + id +
                    "&mode=" + mode +
                    "&hash=" + hash

            val downloadPage = Jsoup.connect(url).get()
            container = downloadPage.select("span").first()!!
                .select("a")
            url = container.attr("href")

            videoList.add(VideoModel("Default",url))

        } catch (e: Exception) { e.printStackTrace() }
    }
}