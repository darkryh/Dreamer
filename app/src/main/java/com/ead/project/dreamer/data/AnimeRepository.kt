package com.ead.project.dreamer.data

import androidx.lifecycle.MutableLiveData
import com.ead.project.dreamer.app.model.AppStatus
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.dao.AnimeBaseDao
import com.ead.project.dreamer.data.database.dao.AnimeProfileDao
import com.ead.project.dreamer.data.database.dao.ChapterDao
import com.ead.project.dreamer.data.database.dao.ChapterHomeDao
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
    private val retrofit: Retrofit
) {


    //CHAPTERS_HOME


    fun insertAllChaptersHome(chapterHomeList: List<ChapterHome>) = chapterHomeDao.insertAll(chapterHomeList)

    suspend fun updateHome(chapterHomeList: List<ChapterHome>) = chapterHomeDao.updateHome(chapterHomeList)

    fun getChaptersHome() : MutableList<ChapterHome> = chapterHomeDao.getList()

    fun getFlowChapterHome() : Flow<List<ChapterHome>> {
        if (Constants.isGooglePolicyActivate()) {
            return chapterHomeDao.getFlowDataListCensured()
        }
        return chapterHomeDao.getFlowDataList()
    }

    fun getChapterHomeReleaseList() : List<ChapterHome> = chapterHomeDao.getReleaseList()


    //ANIME_BASE


    fun insertAllAnimeBase(animBaseList : List<AnimeBase>) = animeBaseDao.insertAll(animBaseList)

    //suspend fun updateAnimeBase(animeBase: AnimeBase) = animeBaseDao.update(animeBase)

    fun getDirectory() : List<AnimeBase> = animeBaseDao.getList()

    fun getAnimeBaseById(id: Int) : AnimeBase = animeBaseDao.getById(id)

    fun getFlowAnimeBaseList(title: String) : Flow<List<AnimeBase>> {
        return animeBaseDao.getFlowDataListByName(title)
    }

    fun getFlowAnimeBaseFromTitle(title : String) : Flow<AnimeBase?> = animeBaseDao.getFlowAnimeBaseFromTitle(title)

    fun checkIfAnimeBaseExist(title: String) = animeBaseDao.checkIfExist(title)


    //ANIME_PROFILE


    fun insertProfile(animeProfile: AnimeProfile) = animeProfileDao.insertProfile(animeProfile)

    suspend fun updateAnimeProfile(animeProfile: AnimeProfile) = animeProfileDao.update(animeProfile)

    fun getProfileList() : List<AnimeProfile> = animeProfileDao.getProfileList()

    fun getProfileReleases() : List<AnimeProfile> = animeProfileDao.getProfileReleases()

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


    //CHAPTERS


    suspend fun insertChapters(chapterList: List<Chapter>) = chapterDao.insertChapters(chapterList)

    suspend fun updateChapter(chapter: Chapter) = chapterDao.update(chapter)

    suspend fun updateChapters(chapterList: List<Chapter>) = chapterDao.updateChapters(chapterList)

    fun getChapterFromId(id : Int) : Chapter? = chapterDao.getChapterFromId(id)

    fun getFlowPreparationProfile(id : Int) : Flow<List<Int>> = chapterDao.getFlowPreparation(id)

    fun getFlowChaptersFromProfile(id :Int) : Flow<List<Chapter>> = chapterDao.getFlowChaptersFromProfile(id)

    fun getFlowChaptersFromProfileInSections(id :Int,start: Int,end: Int) : Flow<List<Chapter>> = chapterDao.getFlowChaptersFromProfileInSections(id,start,end)

    fun getFlowChaptersFromProfileAsc(id :Int) : Flow<List<Chapter>> = chapterDao.getFlowChaptersFromProfileAsc(id)

    fun getFlowChaptersRecord() : Flow<List<Chapter>> = chapterDao.getFlowDataRecords()

    fun getChapterFromTitleAndNumber(title: String, number : Int) : Chapter? = chapterDao.getChapterFromTitleAndNumber(title,number)

    fun getFlowChapterFromTitleAndNumber(title: String, number : Int) : Flow<Chapter?> = chapterDao.getFlowChapterFromTitleAndNumber(title,number)


    //DISCORD API

    private var accessToken : MutableLiveData<AccessToken?>?= null
    private var refreshAccessToken : MutableLiveData<AccessToken?>?= null
    private var user : MutableLiveData<User?>?= null
    private var guildMember : MutableLiveData<GuildMember?>?= null
    private var appStatus : MutableLiveData<AppStatus>?= null
    private var publicity : MutableLiveData<Publicity>?= null


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
        if (accessToken  == null)
            accessToken = MutableLiveData<AccessToken?>()

        val discordService = getService(getRetrofitToken())
        val response : Call<AccessToken?> = discordService.getAccessToken()
        response.enqueue(object : Callback<AccessToken?> {
            override fun onResponse(call: Call<AccessToken?>, response: Response<AccessToken?>) {
                try {
                    if (response.isSuccessful) {
                        accessToken!!.value = response.body()
                    }
                } catch ( e : Exception) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                try{
                    DreamerApp.showLongToast(t.cause!!.message.toString())
                }catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        })
        return accessToken
    }

    fun getRefreshAccessToken() : MutableLiveData<AccessToken?>? {
        if (refreshAccessToken  == null)
            refreshAccessToken = MutableLiveData<AccessToken?>()

        val discordService = getService(getRetrofitRefreshToken())
        val response : Call<AccessToken?> = discordService.getAccessToken()
        response.enqueue(object : Callback<AccessToken?> {
            override fun onResponse(call: Call<AccessToken?>, response: Response<AccessToken?>) {
                try {
                    if (response.isSuccessful) {
                        refreshAccessToken!!.value = response.body()
                    }
                } catch ( e : Exception) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                try{
                    DreamerApp.showLongToast(t.cause!!.message.toString())
                }catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        })
        return refreshAccessToken
    }

    fun getUserData() : MutableLiveData<User?>? {
        if (user  == null)
            user = MutableLiveData<User?>()
        user!!.value = null
        val discordService = getService(getRetrofitAuth())
        val response : Call<User?> = discordService.getCurrentUser()
        response.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                try {
                    if (response.isSuccessful) {
                        user!!.value = response.body()
                    }
                } catch ( e : Exception) {
                    DreamerApp.showLongToast(e.cause!!.message.toString())
                }
            }
            override fun onFailure(call: Call<User?>, t: Throwable) {
                try {
                    DreamerApp.showLongToast(t.cause!!.message.toString())
                }catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        })
        return user
    }

    fun getGuildMember(id : String) : MutableLiveData<GuildMember?>? {
        if (guildMember  == null)
            guildMember = MutableLiveData<GuildMember?>()

        val response : Call<GuildMember?> = getService(retrofit).getGuildMember(id)
        response.enqueue(object : Callback<GuildMember?> {
            override fun onResponse(call: Call<GuildMember?>, response: Response<GuildMember?>) {
                try {
                    if (response.isSuccessful) {
                        guildMember!!.value = response.body()
                    }
                } catch ( e : Exception) {
                    e.printStackTrace()
                }

            }
            override fun onFailure(call: Call<GuildMember?>, t: Throwable) {
                try{
                    DreamerApp.showLongToast(t.cause!!.message.toString())
                }catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        })
        return guildMember
    }

    fun getUserInToGuild(id : String) : MutableLiveData<GuildMember?>? {
        if (guildMember  == null)
            guildMember = MutableLiveData<GuildMember?>()

        val discordService = getService(getRetrofitGuild())
        val response : Call<GuildMember?> = discordService
            .getUserIntoGuild(id)

        response.enqueue(object : Callback<GuildMember?> {
            override fun onResponse(call: Call<GuildMember?>, response: Response<GuildMember?>) {
                try {
                    if (response.isSuccessful) {
                        guildMember!!.value = response.body()
                        DreamerApp.showShortToast("Inicio de Sesión Exitoso!")
                    }
                } catch ( e : Exception) {
                    e.printStackTrace()
                }

            }
            override fun onFailure(call: Call<GuildMember?>, t: Throwable) {
                try{
                    DreamerApp.showLongToast(t.cause!!.message.toString())
                }catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        })
        return guildMember
    }

    fun getAppStatus() : MutableLiveData<AppStatus>? {
        if (appStatus  == null)
            appStatus = MutableLiveData<AppStatus>()

        val appService = getAppService(getRetrofitApp())
        val response : Call<AppStatus> = appService.getAppStatus()
        response.enqueue(object : Callback<AppStatus> {
            override fun onResponse(call: Call<AppStatus>, response: Response<AppStatus>) {
                try {
                    if (response.isSuccessful) {
                        appStatus!!.value = response.body()
                    }
                } catch ( e : Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<AppStatus>, t: Throwable) {
                try{
                    DreamerApp.showLongToast(t.cause!!.message.toString())
                }catch (e : Exception) {
                    e.printStackTrace()
                }
            }

        })
        return appStatus
    }

    fun getPublicityApp() : MutableLiveData<Publicity>? {
        if (publicity  == null)
            publicity = MutableLiveData<Publicity>()

        val appService = getAppService(getRetrofitApp())
        val response : Call<Publicity> = appService.getPublicity()
        response.enqueue(object : Callback<Publicity> {
            override fun onResponse(call: Call<Publicity>, response: Response<Publicity>) {
                try {
                    if (response.isSuccessful) {
                        publicity!!.value = response.body()
                    }
                } catch ( e : Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Publicity>, t: Throwable) {
                try{
                    DreamerApp.showLongToast(t.cause!!.message.toString())
                }catch (e : Exception) {
                    e.printStackTrace()
                }
            }

        })
        return publicity
    }
}