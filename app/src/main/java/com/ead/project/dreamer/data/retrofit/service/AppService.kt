package com.ead.project.dreamer.data.retrofit.service

import com.ead.project.dreamer.app.model.AppStatus
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.app.model.scrapping.*
import retrofit2.Call
import retrofit2.http.GET

interface AppService {

    @GET("app_status.json")
    fun getAppStatus() : Call<AppStatus>

    @GET("publicity.json")
    fun getPublicity() : Call<List<Publicity>>

    @GET("AnimeBaseScrap.json")
    fun getAnimeBaseScrap() : Call<AnimeBaseScrap>

    @GET("AnimeProfileScrap.json")
    fun getAnimeProfileScrap() : Call<AnimeProfileScrap>

    @GET("ChapterHomeScrap.json")
    fun getChapterHomeScrap() : Call<ChapterHomeScrap>

    @GET("ChapterScrap.json")
    fun getChapterScrap() : Call<ChapterScrap>

    @GET("NewsItemScrap.json")
    fun getNewsItemScrap() : Call<NewsItemScrap>

    @GET("NewsItemWebScrap.json")
    fun getNewsItemWebScrap() : Call<NewsItemWebScrap>

    @GET("server_script.json")
    fun getServerScriptScrap() : Call<String>
}