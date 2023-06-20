package com.ead.project.dreamer.data

import com.ead.project.dreamer.app.AppInfo
import com.ead.project.dreamer.app.data.monos_chinos.MonosChinos
import com.ead.project.dreamer.app.data.util.system.getCatch
import com.ead.project.dreamer.app.model.scraper.AnimeBaseScrap
import com.ead.project.dreamer.app.model.scraper.AnimeProfileScrap
import com.ead.project.dreamer.app.model.scraper.ChapterHomeScrap
import com.ead.project.dreamer.app.model.scraper.ChapterScrap
import com.ead.project.dreamer.app.model.scraper.NewsItemScrap
import com.ead.project.dreamer.app.model.scraper.NewsItemWebScrap
import com.ead.project.dreamer.data.database.dao.*
import com.ead.project.dreamer.data.database.model.*
import com.ead.project.dreamer.data.retrofit.interceptor.*
import com.ead.project.dreamer.data.retrofit.service.ApplicationService
import com.ead.project.dreamer.data.retrofit.service.DiscordService
import com.ead.project.dreamer.data.retrofit.service.MonosChinosService
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject


class AnimeRepository @Inject constructor(
    private val chapterHomeDao: ChapterHomeDao,
    private val animeBaseDao: AnimeBaseDao,
    private val animeProfileDao: AnimeProfileDao,
    private val chapterDao: ChapterDao,
    private val newsItemDao: NewsItemDao,
    private val retrofit: Retrofit
) {


    //CHAPTERS_HOME


    suspend fun insertHomeList(chapterHomeList: List<ChapterHome>) = chapterHomeDao.insertAll(chapterHomeList)

    suspend fun updateHomeList(chapterHomeList: List<ChapterHome>) = chapterHomeDao.updateHome(chapterHomeList)

    suspend fun getChaptersHome() : MutableList<ChapterHome> = chapterHomeDao.getChapterHomeList()

    suspend fun getChapterHomeReleaseList() : List<ChapterHome> = chapterHomeDao.getReleaseList()

    fun getFlowChapterHome() : Flow<List<ChapterHome>> = chapterHomeDao.getFlowDataList()

    fun getFlowChapterHomeCensured() : Flow<List<ChapterHome>> = chapterHomeDao.getFlowDataListCensured()


    //ANIME_BASE


    suspend fun insertDirectoryList(animBaseList : List<AnimeBase>) = animeBaseDao.insertAll(animBaseList)

    suspend fun getDirectory() : List<AnimeBase> = animeBaseDao.getList()

    suspend fun getDirectoryById(id: Int) : AnimeBase = animeBaseDao.getById(id)

    fun getFlowAnimeBaseList(title: String) : Flow<List<AnimeBase>> =
        animeBaseDao.getFlowDataListByName(title)

    fun getFlowAnimeBaseListCensured(title: String) :  Flow<List<AnimeBase>> =
        animeBaseDao.getFlowDataListByNameCensured(title)

    fun getFlowAnimeBaseFullList(title: String) : Flow<List<AnimeBase>> =
        animeBaseDao.getFlowDataFullListByName(title)

    fun getFlowAnimeBaseFullListCensured(title: String) : Flow<List<AnimeBase>> =
        animeBaseDao.getFlowDataFullListByNameCensured(title)

    fun getFlowAnimeBaseFromTitle(title : String) : Flow<AnimeBase?> = animeBaseDao.getFlowAnimeBaseFromTitle(title)

    fun checkIfAnimeBaseExist(title: String) = animeBaseDao.checkIfExist(title)


    //ANIME_PROFILE


    suspend fun insertProfile(animeProfile: AnimeProfile) = animeProfileDao.insertProfile(animeProfile)

    suspend fun updateAnimeProfile(animeProfile: AnimeProfile) = animeProfileDao.update(animeProfile)

    suspend fun getProfileList() : List<AnimeProfile> = animeProfileDao.getProfileList()

    suspend fun getProfilesToFix() : List<AnimeProfile> = animeProfileDao.getProfilesToFix()

    suspend fun getProfileReleases() : List<AnimeProfile> = animeProfileDao.getProfileReleases()

    suspend fun getFavoriteProfileReleasesTitles() : List<String> = animeProfileDao.getFavoriteProfileReleasesTitles()

    suspend fun getMostViewedSeries() : List<AnimeProfile> = animeProfileDao.getMostViewedSeries()

    suspend fun getRecommendations(list: List<String>) = animeProfileDao.getRecommendations(list.getCatch(0),list.getCatch(1),list.getCatch(2),list.getCatch(3),list.getCatch(4),list.getCatch(5),list.getCatch(6),list.getCatch(7),list.getCatch(8))

    suspend fun getAnimeProfile(id : Int) : AnimeProfile? = animeProfileDao.getProfile(id)

    fun getFlowAnimeProfile(id : Int) : Flow<AnimeProfile?> = animeProfileDao.getFlowProfile(id)

    fun getFlowRandomProfileListFrom(genre : String, animeProfile: AnimeProfile,limit : Int = 20) :Flow<List<AnimeProfile>> =
        animeProfileDao.getFlowProfileRandomListFrom(genre,animeProfile.rating,animeProfile.id,limit)

    fun getFlowRandomProfileListCensuredFrom(genre : String, animeProfile: AnimeProfile,limit : Int = 20) :Flow<List<AnimeProfile>> =
        animeProfileDao.getFlowProfileRandomListCensuredFrom(genre,animeProfile.rating,animeProfile.id,limit)

    fun getFlowProfileRandomRecommendationsList(): Flow<List<AnimeProfile>> =
        animeProfileDao.getFlowProfileRandomRecommendationsList()

    fun getFlowProfileRandomRecommendationsListCensured(): Flow<List<AnimeProfile>> =
        animeProfileDao.getFlowProfileRandomRecommendationsListCensured()

    fun getFlowLikedDirectory() : Flow<List<AnimeProfile>> = animeProfileDao.getLikeFlowDataList()

    fun getFlowMostViewedSeries() : Flow<List<AnimeProfile>> = animeProfileDao.getFlowMostViewedSeries()


    //CHAPTERS


    suspend fun insertChapterList(chapterList: List<Chapter>) = chapterDao.insertChapters(chapterList)

    suspend fun updateChapterList(chapterList: List<Chapter>) = chapterDao.updateChapters(chapterList)

    suspend fun updateChapter(chapter: Chapter) = chapterDao.update(chapter)

    suspend fun getNotDownloadedChaptersFromId(id: Int) : List<Chapter> = chapterDao.getNotDownloadedChaptersFromId(id)

    suspend fun deleteChaptersById(id: Int) = chapterDao.deleteChaptersById(id)

    suspend fun getChapterFromId(id : Int) : Chapter? = chapterDao.getChapterFromId(id)

    fun getFlowFirstChapterFromProfileId(id: Int) : Flow<Chapter?> = chapterDao.getFlowFirstChapterFromProfileId(id)

    suspend fun getChaptersRecordsFromId(id : Int) : List<Chapter> = chapterDao.getChaptersRecordsFromId(id)

    suspend fun getChaptersToFix() : List<Chapter> = chapterDao.getChaptersToFix()

    suspend fun getChapterFromTitleAndNumber(title: String, number : Int) : Chapter? = chapterDao.getChapterFromTitleAndNumber(title,number)

    suspend fun getPreparationProfile(id : Int) : List<Int> = chapterDao.getPreparation(id)

    fun updateChapterNormal(chapter: Chapter) = chapterDao.updateNormal(chapter)

    fun getFlowChaptersFromProfile(id :Int) : Flow<List<Chapter>> = chapterDao.getFlowChaptersFromProfile(id)

    fun getFlowChaptersFromNumber(id : Int,number: Int) : Flow<List<Chapter>> = chapterDao.getFlowChaptersFromNumber(id,number)

    fun getFlowChaptersFromProfileAsc(id :Int) : Flow<List<Chapter>> = chapterDao.getFlowChaptersFromProfileAsc(id)

    fun getFlowChaptersRecord() : Flow<List<Chapter>> = chapterDao.getFlowDataRecords()

    fun getFlowChapterFromTitleAndNumber(title: String, number : Int) : Flow<Chapter?> = chapterDao.getFlowChapterFromTitleAndNumber(title,number)


    //NEWS ITEM


    suspend fun insertNewsItemList(newsItemList: List<NewsItem>)  = newsItemDao.insertAll(newsItemList)

    suspend fun updateNewsItemList(newsItemList: List<NewsItem>)  = newsItemDao.updateNews(newsItemList)

    suspend fun getNewsItems() : List<NewsItem> = newsItemDao.getNewsItemList()

    fun getFlowNewsItems() : Flow<List<NewsItem>> = newsItemDao.getFlowDataList()

    fun getFlowNewsItemsCensured() : Flow<List<NewsItem>> = newsItemDao.getFlowDataListCensured()


    //DISCORD API
    
    fun getDiscordService(retrofit: Retrofit) : DiscordService =
        retrofit.create(DiscordService::class.java)

    fun getDiscordUserTokenRetrofit() : Retrofit = retrofit.newBuilder()
        .client(OkHttpClient.Builder().addInterceptor(AccessInterceptor()).build()).build()

    fun getDiscordUserRefreshTokenRetrofit() : Retrofit = retrofit.newBuilder()
        .client(OkHttpClient.Builder().addInterceptor(RefreshInterceptor()).build()).build()

    fun getDiscordAuthRetrofit() : Retrofit = retrofit.newBuilder()
        .client(OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()).build()

    fun getDiscordGuildRetrofit() : Retrofit = retrofit.newBuilder()
        .client(OkHttpClient.Builder().addInterceptor(GuildInterceptor()).build()).build()

    @Suppress("unused")
    private fun getDiscordUserAccessTokenDiscord() = retrofit.newBuilder()
    .client(OkHttpClient.Builder().addInterceptor(UserAccessInterceptorInterceptor()).build()).build()


    //APP API

    fun getAppRetrofit() : Retrofit =
        retrofit.newBuilder().baseUrl(AppInfo.API_APP)
            .build()

    fun getAppService(retrofit: Retrofit) : ApplicationService =
        retrofit.create(ApplicationService::class.java)

    fun getAnimeBaseScrap() : AnimeBaseScrap {
        val appService = getAppService(getAppRetrofit())
        val response : Call<AnimeBaseScrap> = appService.getAnimeBaseScrap()
        return response.execute().body()!!
    }

    fun getAnimeProfileScrap() : AnimeProfileScrap {
        val appService = getAppService(getAppRetrofit())
        val response : Call<AnimeProfileScrap> = appService.getAnimeProfileScrap()
        return response.execute().body()!!
    }

    fun getChapterHomeScrap() : ChapterHomeScrap {
        val appService = getAppService(getAppRetrofit())
        val response : Call<ChapterHomeScrap> = appService.getChapterHomeScrap()
        return response.execute().body()!!
    }

    fun getChapterScrap() : ChapterScrap {
        val appService = getAppService(getAppRetrofit())
        val response : Call<ChapterScrap> = appService.getChapterScrap()
        return response.execute().body()!!
    }

    fun getNewsItemScrap() : NewsItemScrap {
        val appService = getAppService(getAppRetrofit())
        val response : Call<NewsItemScrap> = appService.getNewsItemScrap()
        return response.execute().body()!!
    }

    fun getNewsItemWebScrap() : NewsItemWebScrap {
        val appService = getAppService(getAppRetrofit())
        val response : Call<NewsItemWebScrap> = appService.getNewsItemWebScrap()
        return response.execute().body()!!
    }

    fun getServerScript() : String {
        val appService = getAppService(getAppRetrofit())
        val response : Call<String> = appService.getServerScriptScrap()
        return response.execute().body()!!
    }

    // MONOS-CHINOS API

    private fun getMonosChinosRetrofit() : Retrofit =
        retrofit.newBuilder().baseUrl(MonosChinos.API)
            .build()

    fun getMonosChinosService() : MonosChinosService =
        getMonosChinosRetrofit().create(MonosChinosService::class.java)

}