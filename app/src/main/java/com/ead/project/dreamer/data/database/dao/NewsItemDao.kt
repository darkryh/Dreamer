package com.ead.project.dreamer.data.database.dao

import androidx.room.*
import com.ead.project.dreamer.data.database.model.NewsItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<NewsItem>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateNews(list: List<NewsItem>)

    @Query("select * from anime_news_item_table order by id desc")
    suspend fun getNewsItemList() : MutableList<NewsItem>

    @Query("select * from anime_news_item_table order by id desc")
    fun getFlowDataList() : Flow<List<NewsItem>>

    @Query("select * from anime_news_item_table " +
            "where title not like '%hentai%' and title not like '%gelatinos%' " +
            "and title not like '%ecchi%' and title not like '%bragas%' " +
            "and title not like '%desnudo%'  and title not like '%trasero%' " +
            "and title not like '%profana%' and title not like '%adulto%' " +
            "and title not like '%adultos%' and title not like '%pornhub%' " +
            "and title not like '%sexy%' and title not like '%censura%' " +
            "and title not like '%bikini%' and title not like '%+18%' " +
            "and title not like '%sensual%' and title not like '%agua%%gamer%' " +
            "and title not like '%senos%' and title not like '%metamorphosis%' " +
            "and title not like '%oni%%chichi%' and title not like '%agua%%gamer%' " +
            "and title not like '%cosplayer%' and title not like '%waifus%' " +
            "and title not like '%panties%' and title not like '%minifaldas%' " +
            "and title not like '%dakimura%' and title not like '%traje%%baño%' " +
            "and title not like '%ardiente%' and title not like '%overflow%' " +
            "and title not like '%rance%' and title not like '%sexual%' " +
            "and title not like '%parodia%%h%' and title not like '%dxd%' " +
            "and title not like '%artista%%h%' and title not like '%ntr%' " +
            "and title not like '%netorare%%h%' and title not like '%delicioso%' " +
            "and title not like '%vtuber%%baño%' and title not like '%oppai%' " +
            "and title not like '%ia convierte%' and title not like '%pedó%' " +
            "and title not like '%pedo%' and title not like '%norte%' " +
            "and title not like '%falda%' and title not like '%ia le%' " +
            "and title not like '%muslos%' and title not like '%porno%' " +
            "order by id desc")
    fun getFlowDataListCensured() : Flow<List<NewsItem>>

    @Query("select * from anime_news_item_table order by id desc limit 3")
    fun getFlowDataListLimited() : Flow<List<NewsItem>>

    @Query("select * from anime_news_item_table " +
            "where title not like '%hentai%' and title not like '%gelatinos%' " +
            "and title not like '%ecchi%' and title not like '%bragas%' " +
            "and title not like '%desnudo%'  and title not like '%trasero%' " +
            "and title not like '%profana%' and title not like '%adulto%' " +
            "and title not like '%adultos%' and title not like '%pornhub%' " +
            "and title not like '%sexy%' and title not like '%censura%' " +
            "and title not like '%bikini%' and title not like '%+18%' " +
            "and title not like '%sensual%' and title not like '%agua%%gamer%' " +
            "and title not like '%senos%' and title not like '%metamorphosis%' " +
            "and title not like '%oni%%chichi%' and title not like '%agua%%gamer%' " +
            "and title not like '%cosplayer%' and title not like '%waifus%' " +
            "and title not like '%panties%' and title not like '%minifaldas%' " +
            "and title not like '%dakimura%' and title not like '%traje%%baño%' " +
            "and title not like '%ardiente%' and title not like '%overflow%' " +
            "and title not like '%rance%' and title not like '%sexual%' " +
            "and title not like '%parodia%%h%' and title not like '%dxd%' " +
            "and title not like '%artista%%h%' and title not like '%ntr%' " +
            "and title not like '%netorare%%h%' and title not like '%delicioso%' " +
            "and title not like '%vtuber%%baño%' and title not like '%oppai%' " +
            "and title not like '%ia convierte%' and title not like '%pedó%' " +
            "and title not like '%pedo%' and title not like '%norte%' " +
            "and title not like '%falda%' and title not like '%ia le%' " +
            "and title not like '%muslos%' and title not like '%porno%' " +
            "order by id desc limit 3")
    fun getFlowDataListCensuredLimited() : Flow<List<NewsItem>>
}