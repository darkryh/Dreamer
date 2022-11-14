package com.ead.project.dreamer.data.retrofit.service

import com.ead.project.dreamer.app.model.AppStatus
import com.ead.project.dreamer.app.model.Publicity
import com.ead.project.dreamer.app.model.scrapping.*
import retrofit2.Call
import retrofit2.http.GET

interface AppService {

    @GET("app_status.json")
    fun getAppStatus() : Call<AppStatus>

    @GET("Ads/publicity.json")
    fun getPublicity() : Call<List<Publicity>>

    @GET("Scrapper/AnimeBaseScrap.json")
    fun getAnimeBaseScrap() : Call<AnimeBaseScrap>

    @GET("Scrapper/AnimeProfileScrap.json")
    fun getAnimeProfileScrap() : Call<AnimeProfileScrap>

    @GET("Scrapper/ChapterHomeScrap.json")
    fun getChapterHomeScrap() : Call<ChapterHomeScrap>

    @GET("Scrapper/ChapterScrap.json")
    fun getChapterScrap() : Call<ChapterScrap>

    @GET("Scrapper/NewsItemScrap.json")
    fun getNewsItemScrap() : Call<NewsItemScrap>

    @GET("Scrapper/NewsItemWebScrap.json")
    fun getNewsItemWebScrap() : Call<NewsItemWebScrap>

    @GET("Functionality/server_script.json")
    fun getServerScriptScrap() : Call<String>
}