package com.ead.project.dreamer.data.network

import com.ead.project.dreamer.app.data.monos_chinos.MonosChinos
import com.ead.project.dreamer.app.data.news.SomosKudasai
import com.ead.project.dreamer.app.data.util.system.getCatch
import com.ead.project.dreamer.app.data.util.system.toFloatCatch
import com.ead.project.dreamer.app.data.util.system.toIntCatch
import com.ead.project.dreamer.app.model.scraper.AnimeBaseScrap
import com.ead.project.dreamer.app.model.scraper.AnimeProfileScrap
import com.ead.project.dreamer.app.model.scraper.ChapterHomeScrap
import com.ead.project.dreamer.app.model.scraper.ChapterScrap
import com.ead.project.dreamer.app.model.scraper.NewsItemScrap
import com.ead.project.dreamer.app.model.scraper.NewsItemWebScrap
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.data.models.Image
import com.ead.project.dreamer.data.models.NewsItemWeb
import com.ead.project.dreamer.data.models.Title
import com.ead.project.dreamer.data.models.Video
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.domain.apis.app.GetChapterScrap
import com.ead.project.dreamer.domain.apis.app.GetDirectoryScrap
import com.ead.project.dreamer.domain.apis.app.GetHomeScrap
import com.ead.project.dreamer.domain.apis.app.GetNewsItemScrap
import com.ead.project.dreamer.domain.apis.app.GetNewsItemWebScrap
import com.ead.project.dreamer.domain.apis.app.GetProfileScrap
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import javax.inject.Inject

class WebProvider @Inject constructor(
    getHomeScrap: GetHomeScrap,
    getDirectoryScrap: GetDirectoryScrap,
    getProfileScrap: GetProfileScrap,
    getChapterScrap: GetChapterScrap,
    getNewsItemScrap: GetNewsItemScrap,
    getNewsItemWebScrap: GetNewsItemWebScrap
) {

    private val chapterHomeScrap : ChapterHomeScrap = getHomeScrap()
    private val animeBaseScrap : AnimeBaseScrap = getDirectoryScrap()
    private val animeProfileScrap : AnimeProfileScrap = getProfileScrap()
    private val chapterScrap : ChapterScrap = getChapterScrap()

    private val newsItemScrap : NewsItemScrap = getNewsItemScrap()
    private val newsItemWebScrap : NewsItemWebScrap = getNewsItemWebScrap()

    fun getChaptersHome(
        firstChapter: ChapterHome,
    ): List<ChapterHome> {

        val auxChapterList = mutableListOf<ChapterHome>()

        try {
            val doc = Jsoup.connect(MonosChinos.URL).get()

            val rawChapterList = doc.select(chapterHomeScrap.classList)
            var index = rawChapterList.size + 1
            val attrImage = getAttrImage(rawChapterList,chapterHomeScrap.chapterCoverContainer)

            for (rawChapter in rawChapterList) {

                val title = rawChapter.select(chapterHomeScrap.titleContainer).attr("title")
                val chapterCover = rawChapter.select(chapterHomeScrap.chapterCoverContainer).attr(attrImage)
                val chapterNumber = rawChapter.select(chapterHomeScrap.chapterNumberContainer).text().toIntCatch()
                val type = rawChapter.select(chapterHomeScrap.typeContainer).text()
                val reference = rawChapter.select(chapterHomeScrap.referenceContainer).attr("href")

                val chapterHome = ChapterHome(
                    --index,
                    title,
                    chapterCover,
                    chapterNumber,
                    type,
                    reference)

                if (firstChapter.sameData(chapterHome)) break
                else auxChapterList.add(chapterHome)
            }

        } catch (ex: IOException) { ex.printStackTrace() }

        return auxChapterList
    }

    fun requestingData (sectionPos : Int) : List<AnimeBase> {

        val document = Jsoup.connect(MonosChinos.URL + MonosChinos.LIST)
            .userAgent(DreamerRequest.userAgent()).get()

        val auxChapterList = mutableListOf<AnimeBase>()
        val directorySize = requestDirectorySize(document)

        val section = getSections(sectionPos,directorySize)

        val elementsTesting = Jsoup.connect(MonosChinos.URL + MonosChinos.PAGE + 1)
            .userAgent(DreamerRequest.userAgent()).get().getElementsByClass(animeBaseScrap.classList)
        val attrImage = getAttrImage(elementsTesting,animeBaseScrap.imageContainer)
        val animeBaseTest = getAnimeBaseTest(elementsTesting,attrImage)

        if (animeBaseTest.isWorking())
            for (page in section.first until section.second + 1) {

                val url = MonosChinos.URL + MonosChinos.PAGE + page.toString()

                val docPages = Jsoup.connect(url).userAgent(DreamerRequest.userAgent()).get()
                val seriesData = docPages.getElementsByClass(animeBaseScrap.classList)

                for (serie in seriesData) {

                    val title = serie.select(animeBaseScrap.titleContainer).attr("title")
                    val reference = serie.select(animeBaseScrap.referenceContainer).attr("href")
                    val image = serie.select(animeBaseScrap.imageContainer).attr(attrImage)
                    val type = serie.select(animeBaseScrap.typeContainer).text().split(" · ").getCatch(0)
                    val year = serie.select(animeBaseScrap.yearContainer).text().split(" · ").getCatch(1).toIntCatch()

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

    private fun getAnimeBaseTest(elements: Elements,attrImage: String) : AnimeBase = AnimeBase(0, elements.getCatch(0).select(animeBaseScrap.titleContainer).attr("title"), elements.getCatch(0).select(animeBaseScrap.imageContainer).attr(attrImage), elements.getCatch(0).select(animeBaseScrap.referenceContainer).attr("href"), elements.getCatch(0).select(animeBaseScrap.typeContainer).text().split(" · ").getCatch(0), elements.getCatch(0).select(animeBaseScrap.yearContainer).text().split(" · ").getCatch(1).toIntCatch())

    private fun requestDirectorySize(document: Document) : Int {
        val refLinkPages = document.getElementsByClass("page-item")
        return refLinkPages.getCatch(refLinkPages.size - 2).text().toIntCatch()
    }

    fun getAnimeProfile(idProfile: Int,reference: String): AnimeProfile {

        val document = Jsoup.connect(reference).get()

        val attrImageProfile = getAttrImage(document,animeProfileScrap.profilePhotoContainer)
        val attrImageCover = getAttrImage(document,animeProfileScrap.coverPhotoContainer)

        val title = document.select(animeProfileScrap.titleContainer).getCatch(0).text()
        val titleAlternate = document.select(animeProfileScrap.titleAlternativeContainer).getCatch(0).text()
        val state = document.select(animeProfileScrap.stateContainer).text()
        val description = document.select(animeProfileScrap.descriptionContainer).getCatch(2).text().removeSuffix("Ver menos")
        val rating = document.select(animeProfileScrap.ratingContainer).attr("data-rating").toFloatCatch()
        val profilePhoto = document.select(animeProfileScrap.profilePhotoContainer).attr(attrImageProfile)
        val coverPhoto = document.select(animeProfileScrap.coverPhotoContainer).attr(attrImageCover)
        val genre : List<String> = try { document.select(animeProfileScrap.genresContainer).map { it.text() } } catch (e : Exception) { emptyList()}
        val rawGenre = genre.toString()
        val date = document.select(animeProfileScrap.dateContainer).text()
        val size = document.getElementsByClass(animeProfileScrap.sizeContainer).size.toString().toIntCatch()

        return AnimeProfile(
            idProfile,coverPhoto,profilePhoto,title,titleAlternate, rating,
            state,description,date,genre,rawGenre,size
        )
    }

    fun getChaptersFromProfile(
        lastChapter: Chapter,
        reference : String,
        idProfile : Int) : List<Chapter> {

        val document = Jsoup.connect(reference).get()

        val elementsChapters = document.getElementsByClass(chapterScrap.classList).reversed()
        val title = document.select(chapterScrap.titleContainer).text()
        val attrImage = getAttrImage(elementsChapters.first(),chapterScrap.coverContainer)

        val chaptersList: MutableList<Chapter> = ArrayList()

        for (element in elementsChapters) {
            val number = element.attr("data-episode").toIntCatch()
            val cover = element.select(chapterScrap.coverContainer).attr(attrImage)
            val chapterReference = element.select(chapterScrap.referenceContainer).attr("href")
            val chapter = Chapter(
                0,
                idProfile,
                title,
                cover,
                number,
                chapterReference
            )
            if (lastChapter.sameData(chapter)) break
            else chaptersList.add(chapter)
        }
        return chaptersList
    }

    fun getNews(firstNewItem : NewsItem) : List<NewsItem> {
        val auxNewsList = mutableListOf<NewsItem>()
        try {
            val doc = Jsoup.connect(SomosKudasai.URL).get()

            val rawNewsItemList = doc.select(newsItemScrap.classList)
            var index = rawNewsItemList.size + 1

            for (rawNewsItem in rawNewsItemList) {
                val title = rawNewsItem.select(newsItemScrap.titleContainer).text()
                val type = rawNewsItem.select(newsItemScrap.typeContainer)
                    .text()
                val cover = rawNewsItem.select(newsItemScrap.coverContainer)[0].attr("src")
                val reference = rawNewsItem.select(newsItemScrap.referenceContainer)[0].attr("href")
                val date = rawNewsItem.select(newsItemScrap.dateContainer).text()
                val newsItem = NewsItem(--index,title,cover,type,date,reference)

                if (firstNewItem.equalsContent(newsItem)) break
                else auxNewsList.add(newsItem)
            }

        } catch (ex: IOException) { ex.printStackTrace() }

        return auxNewsList
    }

    fun getWebPageNews(reference: String) : NewsItemWeb? {
        return try {
            val doc = Jsoup.connect(reference).get()

            val sectionHeader = doc.select(newsItemWebScrap.headerContainer)

            val type = sectionHeader.select(newsItemWebScrap.typeContainer).text()
            val title = sectionHeader.select(newsItemWebScrap.titleContainer).text()
            val author = sectionHeader.select(newsItemWebScrap.authorContainer).text()
            val date = sectionHeader.select(newsItemWebScrap.dateContainer).text()
            val cover = sectionHeader.select(newsItemWebScrap.coverContainer).attr("src")

            val sectionBody = doc.select(newsItemWebScrap.bodyContainer).first()?.children()
            val bodyList : MutableList<Any> = mutableListOf()

            if (sectionBody != null)
                for (item in sectionBody) {
                    when(item.tagName()) {
                        "p" -> bodyList.add(item.text())
                        "div" -> if (item.className() == "wp-block-image"
                            || item.className() == "im black-bg z-1") {
                            val fChild = item.child(0)
                            if (fChild.tagName() == "figure") {
                                val sChild = fChild.child(0)
                                bodyList.add(Image(sChild.attr("src")))
                            }
                            else
                                if (fChild.tagName() == "img")
                                    bodyList.add(Image(fChild.attr("src")))
                                else
                                    bodyList.add(Image("null"))
                        }
                        "figure" -> {
                            for (miniItem in item.children()) {
                                if (miniItem.tagName() == "figure") {
                                    bodyList.add(Image(miniItem.child(0).attr("src")))
                                }
                                else {
                                    if (miniItem.tagName() == "img")
                                        bodyList.add(Image(miniItem.attr("src")))
                                    else
                                        bodyList.add(Image("null"))
                                }
                            }
                            val child = item.children().last()
                            child?.let {
                                if (it.tagName() == "figcaption" ) {
                                    val insideChild =  it.child(0)
                                    if (insideChild.tagName() == "mark")
                                        bodyList.add(Title(insideChild.text(),"random"))
                                }
                            }
                        }
                        "center" -> {
                            val child = item.child(0)
                            if (child.tagName() == "video") bodyList.add(Video(child.attr("src"),false))
                            //else if (child.tagName() == "iframe") bodyList.add(Video(child.attr("src"),true))
                        }
                        "h5","h4","h3","h2" -> bodyList.add(Title(item.text(),item.tagName()))
                        "ul" -> bodyList.add(item.children().map { " · " + it.text() })
                        "iframe" -> bodyList.add(Video(item.attr("src"),true))
                        "video" -> bodyList.add(Video(item.attr("src"),false))
                        else -> bodyList.add("null")
                    }

                }

            val sectionFooter = doc.select(newsItemWebScrap.footerContainer)

            val photoAuthor = sectionFooter.select(newsItemWebScrap.photoAuthorContainer).attr("src")
            val authorFooter = sectionFooter.select(newsItemWebScrap.authorFooter).text()
            val authorWords = sectionFooter.select(newsItemWebScrap.authorWords).text()

            NewsItemWeb(title,author,cover,type,date,bodyList,photoAuthor,authorFooter,authorWords)
        } catch (ex: IOException) { null }
    }

    private fun getSections(pos : Int, size : Int) : Pair<Int,Int> {
        val portion = size / 3
        return when (pos) {
            1 -> { Pair(1,portion) }
            2 -> { Pair(portion + 1,(portion * 2)) }
            3 -> { Pair((portion * 2) + 1,size) }
            else -> Pair(0,size)
        }
    }

    private fun getAttrImage(document: Document, query : String) : String {
        val src = document.select(query).attr("src")
        return if(isImageSrcWorking(src)) "src" else "data-src"
    }

    private fun getAttrImage(classList : Elements, query : String) : String {
        val src = classList.first()?.select(query)?.attr("src")
        return if(isImageSrcWorking(src)) "src" else "data-src"
    }

    private fun getAttrImage(element: Element , query : String) : String {
        val src = element.select(query).attr("src")
        return if(isImageSrcWorking(src)) "src" else "data-src"
    }

    private fun isImageSrcWorking(src : String?) : Boolean = src != null
            && src != MonosChinos.CAP_BLANK_MC2
            && src != MonosChinos.CAP_BLANK_ANIME_MC2
            && src.isNotEmpty()
}