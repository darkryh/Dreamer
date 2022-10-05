package com.ead.project.dreamer.data

import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.app.model.AppStatus
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.app.model.scrapping.*
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.getCatch
import com.ead.project.dreamer.data.database.dao.*
import com.ead.project.dreamer.data.database.model.*
import com.ead.project.dreamer.data.retrofit.interceptor.*
import com.ead.project.dreamer.data.retrofit.model.discord.*
import com.ead.project.dreamer.data.retrofit.service.AppService
import com.ead.project.dreamer.data.retrofit.service.DiscordService
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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


    suspend fun insertAllChaptersHome(chapterHomeList: List<ChapterHome>) = chapterHomeDao.insertAll(chapterHomeList)

    suspend fun updateHome(chapterHomeList: List<ChapterHome>) = chapterHomeDao.updateHome(chapterHomeList)

    suspend fun getChaptersHome() : MutableList<ChapterHome> = chapterHomeDao.getChapterHomeList()

    suspend fun getChapterHomeReleaseList() : List<ChapterHome> = chapterHomeDao.getReleaseList()

    fun getFlowChapterHome() : Flow<List<ChapterHome>> {
        if (Constants.isGooglePolicyActivate()) {
            return chapterHomeDao.getFlowDataListCensured()
        }
        return chapterHomeDao.getFlowDataList()
    }


    //ANIME_BASE


    suspend fun insertAllAnimeBase(animBaseList : List<AnimeBase>) = animeBaseDao.insertAll(animBaseList)

    //suspend fun updateAnimeBase(animeBase: AnimeBase) = animeBaseDao.update(animeBase)

    suspend fun getDirectory() : List<AnimeBase> = animeBaseDao.getList()

    suspend fun getAnimeBaseById(id: Int) : AnimeBase = animeBaseDao.getById(id)

    fun getFlowAnimeBaseList(title: String) : Flow<List<AnimeBase>> {
        return animeBaseDao.getFlowDataListByName(title)
    }

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

    fun getFlowAnimeProfile(id : Int) : Flow<AnimeProfile?> = animeProfileDao.getFlowProfile(id)

    fun getFlowRandomProfileListFrom(genre : String, rating : Float,id: Int,limit : Int = 20) :Flow<List<AnimeProfile>> {
        if (Constants.isGooglePolicyActivate()) {
            return animeProfileDao.getFlowProfileRandomListCensuredFrom(genre,rating,id,limit)
        }
        return animeProfileDao.getFlowProfileRandomListFrom(genre,rating,id,limit)
    }


    fun getFlowProfileRandomRecommendationsList(): Flow<List<AnimeProfile>> {
        if (Constants.isGooglePolicyActivate()) {
            return animeProfileDao.getFlowProfileRandomRecommendationsListCensured()
        }
        return animeProfileDao.getFlowProfileRandomRecommendationsList()
    }

    fun getFlowLikedDirectory() : Flow<List<AnimeProfile>> = animeProfileDao.getLikeFlowDataList()

    fun getFlowMostViewedSeries() : Flow<List<AnimeProfile>> = animeProfileDao.getFlowMostViewedSeries()

    //CHAPTERS


    suspend fun insertChapters(chapterList: List<Chapter>) = chapterDao.insertChapters(chapterList)

    suspend fun updateChapter(chapter: Chapter) = chapterDao.update(chapter)

    suspend fun updateChapters(chapterList: List<Chapter>) = chapterDao.updateChapters(chapterList)

    suspend fun deleteChaptersById(id: Int) = chapterDao.deleteChaptersById(id)

    suspend fun getChapterFromId(id : Int) : Chapter? = chapterDao.getChapterFromId(id)

    suspend fun getChaptersToFix() : List<Chapter> = chapterDao.getChaptersToFix()

    suspend fun getChapterFromTitleAndNumber(title: String, number : Int) : Chapter? = chapterDao.getChapterFromTitleAndNumber(title,number)

    fun getFlowPreparationProfile(id : Int) : Flow<List<Int>> = chapterDao.getFlowPreparation(id)

    fun getFlowChaptersFromProfile(id :Int) : Flow<List<Chapter>> = chapterDao.getFlowChaptersFromProfile(id)

    fun getFlowChaptersFromProfileAsc(id :Int) : Flow<List<Chapter>> = chapterDao.getFlowChaptersFromProfileAsc(id)

    fun getFlowChaptersRecord() : Flow<List<Chapter>> = chapterDao.getFlowDataRecords()

    fun getFlowChapterFromTitleAndNumber(title: String, number : Int) : Flow<Chapter?> = chapterDao.getFlowChapterFromTitleAndNumber(title,number)


    //NEWS ITEM

    suspend fun insertAllNewsItems(newsItemList: MutableList<NewsItem>)  = newsItemDao.insertAll(newsItemList)

    suspend fun updateNews(newsItemList: MutableList<NewsItem>)  = newsItemDao.updateNews(newsItemList)

    suspend fun getNewsItems() : MutableList<NewsItem> = newsItemDao.getNewsItemList()

    fun getFlowNewsItems() : Flow<List<NewsItem>> = newsItemDao.getFlowDataList()

    //DISCORD API

    private var accessToken : MutableLiveData<AccessToken?>?= null
    private var refreshAccessToken : MutableLiveData<AccessToken?>?= null
    private var user : MutableLiveData<User?>?= null
    private var guildMember : MutableLiveData<GuildMember?>?= null
    private var appStatus : MutableLiveData<AppStatus>?= null
    private var publicity : MutableLiveData<List<Publicity>>?= null


    private fun getRetrofitToken() = retrofit.newBuilder()
        .client(OkHttpClient.Builder().addInterceptor(AccessInterceptor()).build()).build()

    /*private fun getRetrofitUserToken() = retrofit.newBuilder()
        .client(OkHttpClient.Builder().addInterceptor(UserAccessInterceptorInterceptor()).build()).build()*/

    private fun getRetrofitRefreshToken() = retrofit.newBuilder()
        .client(OkHttpClient.Builder().addInterceptor(RefreshInterceptor()).build()).build()

    private fun getRetrofitAuth() = retrofit.newBuilder()
        .client(OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()).build()

    private fun getRetrofitGuild() = retrofit.newBuilder()
        .client(OkHttpClient.Builder().addInterceptor(GuildInterceptor()).build()).build()

    private fun getRetrofitApp() = retrofit.newBuilder()
        .baseUrl(Constants.API_APP).build()

    private fun getService(retrofit: Retrofit) = retrofit.create(DiscordService::class.java)

    private fun getAppService(retrofit: Retrofit) = retrofit.create(AppService::class.java)

    fun getAccessToken() : MutableLiveData<AccessToken?>? {
        if (accessToken  == null) accessToken = MutableLiveData<AccessToken?>()

        val discordService = getService(getRetrofitToken())
        val response : Call<AccessToken?> = discordService.getAccessToken()
        response.enqueue(object : Callback<AccessToken?> {
            override fun onResponse(call: Call<AccessToken?>, response: Response<AccessToken?>) {
                try {
                    if (response.isSuccessful) accessToken?.value = response.body()
                } catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return accessToken
    }

    fun getRefreshAccessToken() : MutableLiveData<AccessToken?>? {
        if (refreshAccessToken  == null) refreshAccessToken = MutableLiveData<AccessToken?>()

        val discordService = getService(getRetrofitRefreshToken())
        val response : Call<AccessToken?> = discordService.getAccessToken()
        response.enqueue(object : Callback<AccessToken?> {
            override fun onResponse(call: Call<AccessToken?>, response: Response<AccessToken?>) {
                try {
                    if (response.isSuccessful) refreshAccessToken?.value = response.body()
                } catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return refreshAccessToken
    }

    fun getUserData() : MutableLiveData<User?>? {
        if (user  == null) user = MutableLiveData<User?>()

        user?.value = null
        val discordService = getService(getRetrofitAuth())
        val response : Call<User?> = discordService.getCurrentUser()
        response.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                try {
                    if (response.isSuccessful) user?.value = response.body()
                } catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<User?>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return user
    }

    fun getGuildMember(id : String) : MutableLiveData<GuildMember?>? {
        if (guildMember  == null) guildMember = MutableLiveData<GuildMember?>()

        val response : Call<GuildMember?> = getService(retrofit).getGuildMember(id)
        response.enqueue(object : Callback<GuildMember?> {
            override fun onResponse(call: Call<GuildMember?>, response: Response<GuildMember?>) {
                try {
                    if (response.isSuccessful) guildMember?.value = response.body()
                } catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<GuildMember?>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return guildMember
    }

    fun getUserInToGuild(id : String) : MutableLiveData<GuildMember?>? {
        if (guildMember  == null) guildMember = MutableLiveData<GuildMember?>()

        val discordService = getService(getRetrofitGuild())
        val response : Call<GuildMember?> = discordService.getUserIntoGuild(id)

        response.enqueue(object : Callback<GuildMember?> {
            override fun onResponse(call: Call<GuildMember?>, response: Response<GuildMember?>) {
                try {
                    if (response.isSuccessful) {
                        guildMember?.value = response.body()
                        DreamerApp.showShortToast("Inicio de Sesión Exitoso!")
                    }
                } catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<GuildMember?>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return guildMember
    }

    fun getAppStatus() : MutableLiveData<AppStatus>? {
        if (appStatus  == null) appStatus = MutableLiveData<AppStatus>()

        val appService = getAppService(getRetrofitApp())
        val response : Call<AppStatus> = appService.getAppStatus()
        response.enqueue(object : Callback<AppStatus> {
            override fun onResponse(call: Call<AppStatus>, response: Response<AppStatus>) {
                try {
                    if (response.isSuccessful) appStatus?.value = response.body()
                } catch ( e : Exception) { e.printStackTrace() }
            }
            override fun onFailure(call: Call<AppStatus>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return appStatus
    }

    fun getPublicityApp() : MutableLiveData<List<Publicity>>? {
        if (publicity  == null)
            publicity = MutableLiveData<List<Publicity>>()

        val appService = getAppService(getRetrofitApp())
        val response : Call<List<Publicity>> = appService.getPublicity()
        response.enqueue(object : Callback<List<Publicity>> {
            override fun onResponse(call: Call<List<Publicity>>, response: Response<List<Publicity>>) {
                try {
                    if (response.isSuccessful) publicity?.value = response.body()
                } catch ( e : Exception) { e.printStackTrace() }
            }

            override fun onFailure(call: Call<List<Publicity>>, t: Throwable) {
                DreamerApp.showLongToast(t.cause?.message.toString())
            }
        })
        return publicity
    }

    fun getAnimeBaseScrap() : AnimeBaseScrap {
        val appService = getAppService(getRetrofitApp())
        val response : Call<AnimeBaseScrap> = appService.getAnimeBaseScrap()
        return response.execute().body()!!
    }

    fun getAnimeProfileScrap() : AnimeProfileScrap {
        val appService = getAppService(getRetrofitApp())
        val response : Call<AnimeProfileScrap> = appService.getAnimeProfileScrap()
        return response.execute().body()!!
    }

    fun getChapterHomeScrap() : ChapterHomeScrap {
        val appService = getAppService(getRetrofitApp())
        val response : Call<ChapterHomeScrap> = appService.getChapterHomeScrap()
        return response.execute().body()!!
    }

    fun getChapterScrap() : ChapterScrap {
        val appService = getAppService(getRetrofitApp())
        val response : Call<ChapterScrap> = appService.getChapterScrap()
        return response.execute().body()!!
    }

    fun getNewsItemScrap() : NewsItemScrap {
        val appService = getAppService(getRetrofitApp())
        val response : Call<NewsItemScrap> = appService.getNewsItemScrap()
        return response.execute().body()!!
    }

    fun getNewsItemWebScrap() : NewsItemWebScrap {
        val appService = getAppService(getRetrofitApp())
        val response : Call<NewsItemWebScrap> = appService.getNewsItemWebScrap()
        return response.execute().body()!!
    }


    fun getServerScript() : String {
        val appService = getAppService(getRetrofitApp())
        val response : Call<String> = appService.getServerScriptScrap()
        return response.execute().body()!!
    }
}