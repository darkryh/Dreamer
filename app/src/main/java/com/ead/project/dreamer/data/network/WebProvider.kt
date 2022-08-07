package com.ead.project.dreamer.data.network

import android.util.Log
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.*
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.ArrayList

class WebProvider @Inject constructor() {

    fun getChaptersHome(
        firstChapter: ChapterHome
    ): MutableList<ChapterHome> {

        val auxChapterList = mutableListOf<ChapterHome>()

        try {
            val doc =
                Jsoup.connect(Constants.BASE_URL)
                    .get()

            val rawChapterList = doc.select("div.col.col-md-6.col-lg-2.col-6")

            var index = rawChapterList.size + 1

            for (rawChapter in rawChapterList) {

                val a = rawChapter.select("a")

                val title = a.attr("title")

                val animeDiv = rawChapter.select("div.animes").select("div.animeimgdiv")
                val chapterCover = animeDiv
                    .select("img")
                    .attr("src")

                val positioning = animeDiv.select("div.hoverdiv").select("div.positioning")

                val chapterNumber = positioning.select("p").text().toInt()
                val type = positioning.select("button").text()
                val reference = a.attr("href")

                val chapterHome = ChapterHome(
                    --index,
                    title,
                    chapterCover,
                    chapterNumber,
                    type,
                    reference)

                if (ChapterHome.sameData(firstChapter,chapterHome))
                    break
                else
                    auxChapterList.add(chapterHome)
            }

        } catch (ex: IOException) { ex.printStackTrace() }

        return auxChapterList
    }

    fun requestingData (sectionPos : Int) : MutableList<AnimeBase> {

        val document = Jsoup.connect(Constants.BASE_URL + Constants.LIST)
            .userAgent(DreamerRequest.userAgent()).get()

        val auxChapterList = mutableListOf<AnimeBase>()
        val directorySize = requestDirectorySize(document)

        val section = getSections(sectionPos,directorySize,
            zeroNeeded = false,
            extraFinal = false)

        for (page in section.first until section.second + 1) {

            val url = Constants.BASE_URL + Constants.PAGE + page.toString()

            val docPages = Jsoup.connect(url).userAgent(DreamerRequest.userAgent()).get()

            val seriesData = docPages.getElementsByClass("col-md-4 col-lg-2 col-6")

            for (serie in seriesData) {

                val a = serie.select("a")
                val title = a.attr("title")
                val reference = a.attr("href")

                val series = serie.select("div.series")
                val image = series.select("div.seriesimg")
                    .select("img")
                    .attr("src")

                val arrayInfo = serie.select("div.seriesdetails")
                    .select("span.seriesinfo").text().split(" · ")
                val type = arrayInfo[0]
                val year = arrayInfo[1].toInt()


                auxChapterList.add(
                    AnimeBase(
                        0,
                        title,
                        image,
                        reference,
                        type,
                        year
                    )
                )
            }
        }
        return auxChapterList
    }

    private fun requestDirectorySize(document: Document) : Int {
        val refLinkPages = document.getElementsByClass("page-item")
        return refLinkPages[refLinkPages.size - 2].text().toInt()
    }

    fun getAnimeProfile(idProfile: Int,reference: String): AnimeProfile {

        val document = Jsoup.connect(reference)
                .get()
        val main = document.select("div.heromain")

        val title = main.select("h1")[0].text()
        val state = main.select("div.butns").select("button.btn1").text()
        val description = main.select("p")[2].text().removeSuffix("Ver menos")
        val rating = main.select("div.heroslidico").attr("data-rating")
        val profilePhoto = document
            .getElementsByClass("chapterpic").select("img").attr("src")
        val coverPhoto = main.select("div.herobg").select("img").attr("src")

        val navProfile = document.getElementsByClass("breadcrumb")
        val genre : List<String> = navProfile[0].select("li.breadcrumb-item").map { it.text() }
        val rawGenre = genre.toString()
        val date = navProfile[1].select("li.breadcrumb-item").text()
        val size = document.getElementsByClass("col-item").size.toString()

        return AnimeProfile(
            idProfile,coverPhoto,profilePhoto,title,rating.toFloat(),
            state,description,date,genre,rawGenre,size.toInt()
        )
    }

    fun getChaptersFromProfile(
        lastChapter: Chapter,
        reference : String,
        idProfile : Int) : MutableList<Chapter> {

        val document =
            Jsoup.connect(reference)
                .get()

        val title = document.select("div.chapterdetails").select("h1").text()

        val elementsChapters = document.getElementsByClass("col-item").reversed()

        val chaptersList: MutableList<Chapter> = ArrayList()


        for (i in elementsChapters.indices) {

            val number = elementsChapters[i].attr("data-episode").toInt()
            val cover = elementsChapters[i]
                .select("div.animedtlsmain")
                .select("div.animeimgdiv")
                .select("img").attr("src")
            val chapterReference = elementsChapters[i].select("a").attr("href")
            val chapter = Chapter(
                0,
                idProfile,
                title,
                cover,
                number,
                chapterReference
            )
            if (Chapter.sameData(lastChapter,chapter))
                break
            else
                chaptersList.add(chapter)
        }
        return chaptersList
    }

    private fun getSections(pos : Int, size : Int, zeroNeeded : Boolean,extraFinal : Boolean) : Pair<Int,Int> {

        val portion = size / 3
        var initExtra = 0
        var finalExtra = 0

        if (!zeroNeeded)
            initExtra = 1

        if (extraFinal)
            finalExtra = 1

        when (pos) {
            1 -> {
                val init = (portion * 0) + initExtra
                val final = (portion * 1) + finalExtra
                return Pair(init,final)
            }
            2 -> {
                val init = (portion * 1) + 1
                val final = (portion * 2) + finalExtra
                return Pair(init,final)
            }
            3 -> {
                val init = (portion * 2) + 1
                return Pair(init,size)
            }
            else ->
                return Pair(0,size)
        }
    }
}