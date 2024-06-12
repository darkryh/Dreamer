package com.ead.project.dreamer.data.network

import android.content.Context
import com.ead.lib.somoskudasai.SomosKudasai
import com.ead.lib.somoskudasai.models.html_tags.H1
import com.ead.lib.somoskudasai.models.html_tags.H2
import com.ead.lib.somoskudasai.models.html_tags.H3
import com.ead.lib.somoskudasai.models.html_tags.H4
import com.ead.lib.somoskudasai.models.html_tags.H5
import com.ead.lib.somoskudasai.models.html_tags.Iframe
import com.ead.lib.somoskudasai.models.html_tags.P
import com.ead.lib.somoskudasai.models.html_tags.Ul
import com.ead.project.dreamer.app.data.monos_chinos.MonosChinos
import com.ead.project.dreamer.app.data.util.system.getCatch
import com.ead.project.dreamer.app.data.util.system.toIntCatch
import com.ead.project.dreamer.app.model.scraper.AnimeBaseScrap
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.database.model.NewsItem
import com.ead.project.dreamer.data.models.ChapterComparison
import com.ead.project.dreamer.data.models.Image
import com.ead.project.dreamer.data.models.NewsItemWeb
import com.ead.project.dreamer.data.models.Title
import com.ead.project.dreamer.data.models.Video
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.domain.apis.app.GetDirectoryScrap
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import javax.inject.Inject

class WebProvider @Inject constructor(
    private val getDirectoryScrap: GetDirectoryScrap
) {

    private var animeBaseScrap : AnimeBaseScrap? = null

    private val profileSeoRegex = "anime/([^/]+)".toRegex()

    suspend fun getChaptersHome(
        context: Context
    ): List<ChapterHome> {

        val home = com.ead.lib.monoschinos.MonosChinos
            .builder(context)
            .homePage()
            .get() ?: return emptyList()

        return home
            .lastChapters.mapIndexed { index, chapter ->
                ChapterHome(
                    id = index,
                    title = chapter.title,
                    chapterCover = chapter.image,
                    reference = chapter.url,
                    chapterNumber = chapter.number,
                    type = chapter.type
                )
            }
    }

    suspend  fun requestingData (sectionPos : Int,context: Context) : List<AnimeBase> {

        val animeBaseScrap = animeBaseScrap?:getDirectoryScrap().also { animeBaseScrap = it }

        val document = Jsoup.connect(MonosChinos.URL + MonosChinos.LIST)
            .userAgent(DreamerRequest.userAgent()).get()

        val auxChapterList = mutableListOf<AnimeBase>()
        val directorySize = requestDirectorySize(document)

        val section = getSections(sectionPos,directorySize)

        val elementsTesting = Jsoup.connect(MonosChinos.URL + MonosChinos.PAGE + 1)
            .userAgent(DreamerRequest.userAgent()).get().select(animeBaseScrap.classList)
        val attrImage = getAttrImage(elementsTesting,animeBaseScrap.imageContainer)
        val animeBaseTest = getAnimeBaseTest(animeBaseScrap,elementsTesting,attrImage)


        if (animeBaseTest.isWorking())
            for (page in section.first until section.second + 1) {

                auxChapterList.addAll(
                    com.ead.lib.monoschinos.MonosChinos
                        .builder(context)
                        .directoryPage(page)
                        .get()
                        .map {
                            AnimeBase(
                                id = 0,
                                title = it.title,
                                cover = it.image,
                                reference = it.url,
                                type = it.type,
                                year = it.year
                            )
                        }
                )
            }

        return auxChapterList
    }

    private fun getAnimeBaseTest(animeBaseScrap: AnimeBaseScrap,elements: Elements,attrImage: String) : AnimeBase = AnimeBase(0, elements.getCatch(0).select(animeBaseScrap.titleContainer).text(), elements.getCatch(0).select(animeBaseScrap.imageContainer).attr(attrImage), elements.getCatch(0).select(animeBaseScrap.referenceContainer).attr("href"), elements.getCatch(0).select(animeBaseScrap.typeContainer).text().split(" · ").getCatch(0), elements.getCatch(0).select(animeBaseScrap.yearContainer).text().split(" · ").getCatch(1).toIntCatch())

    private fun requestDirectorySize(document: Document) : Int {
        val refLinkPages = document.getElementsByClass("page-link")
        return refLinkPages.getCatch(refLinkPages.size - 2).text().toIntCatch()
    }

    suspend fun getAnimeProfile(idProfile: Int,reference: String,context: Context): AnimeProfile {

        val seo = profileSeoRegex.find(reference)?.groupValues?.get(1) ?: "null"

        return com.ead.lib.monoschinos.MonosChinos
            .builder(context)
            .animeDetailPage(seo)
            .get()
            ?.let {
                AnimeProfile(
                    id = idProfile,
                    coverPhoto = it.coverImage,
                    profilePhoto = it.coverImage,
                    title = it.title,
                    titleAlternate = it.alternativeTitle ?:"",
                    rating = -1f,
                    state = it.status,
                    description = it.synopsis,
                    date = it.release,
                    genres = it.genres,
                    rawGenres = it.genres.toString(),
                    size = 0,
                )
            }  ?: AnimeProfile.fake(idProfile)
    }

    suspend fun getChaptersFromProfile(
        reference: String,
        idProfile: Int,
        title : String,
        context: Context
    ) : List<ChapterComparison> {

        val seo = profileSeoRegex.find(reference)?.groupValues?.get(1) ?: return emptyList()

        return com.ead.lib.monoschinos.MonosChinos
            .builder(context)
            .chaptersPage(seo)
            .get()
            .map {
                ChapterComparison(
                    idProfile = idProfile,
                    title = title,
                    cover = it.image,
                    number = it.number,
                    reference = it.url
                )
            }
    }

    suspend fun getNews(): List<NewsItem> {
        return SomosKudasai
            .getHome()
            ?.recentNews
            ?.mapIndexed { index, newsPreview ->
                NewsItem(
                    id = index,
                    title = newsPreview.title,
                    cover = newsPreview.image,
                    reference = newsPreview.url,
                    type = newsPreview.type ?: "unknown",
                    date = newsPreview.date,
                )
            } ?: emptyList()
    }

    suspend  fun getWebPageNews(reference: String) : NewsItemWeb? {
        return try {
            SomosKudasai
                .getNewsInfo(reference)
                ?.let { news ->
                    NewsItemWeb(
                        title = news.title,
                        author = news.author,
                        cover = news.image,
                        type = news.type,
                        date = news.date,
                        photoAuthor = news.authorImage,
                        authorFooter = news.author,
                        authorWords = news.authorWords,
                        bodyList = news.structure.mapNotNull {
                            when (it) {
                                is P -> {
                                    it.text
                                }
                                is com.ead.lib.somoskudasai.models.html_tags.Image -> Image(
                                    source = it.src,
                                )
                                is H1 -> Title(it.text, "h1")
                                is H2 -> Title(it.text, "h2")
                                is H3 -> Title(it.text, "h3")
                                is H4 -> Title(it.text, "h4")
                                is H5 -> Title(it.text, "h5")
                                is Ul -> it.items
                                is Iframe -> Video(it.url, isEmbedded = true)
                                is com.ead.lib.somoskudasai.models.html_tags.Video -> Video(
                                    it.src,
                                    isEmbedded = false
                                )
                                else -> null
                            }
                        }
                    )
                }
        } catch (e: IOException) {
            null
        }
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

    private fun getAttrImage(classList : Elements, query : String) : String {
        val src = classList.first()?.select(query)?.attr("src")
        return if(isImageSrcWorking(src)) "src" else "data-src"
    }

    private fun isImageSrcWorking(src : String?) : Boolean = src != null
            && src != MonosChinos.CAP_BLANK_MC2
            && src != MonosChinos.CAP_BLANK_ANIME_MC2
            && src.isNotEmpty()
}