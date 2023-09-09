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
            "where title not like '%hentai%' " +
            "and title not like '%ecchi%' and title not like '%bragas%' " +
            "and title not like '%desnudo%'  and title not like '%trasero%' " +
            "and title not like '%profana%' and title not like '%adulto%' " +
            "and title not like '%adultos%' and title not like '%pornhub%' " +
            "and title not like '%sexy%' and title not like '%censura%' " +
            "and title not like '%bikini%' and title not like '%+18%' " +
            "and title not like '%sensual%' and title not like '%agua%gamer%' " +
            "and title not like '%porno%' order by id desc")
    fun getFlowDataListCensured() : Flow<List<NewsItem>>

    @Query("select * from anime_news_item_table order by id desc limit 3")
    fun getFlowDataListLimited() : Flow<List<NewsItem>>

    @Query("select * from anime_news_item_table " +
            "where title not like '%hentai%' " +
            "and title not like '%ecchi%' and title not like '%bragas%' " +
            "and title not like '%desnudo%'  and title not like '%trasero%' " +
            "and title not like '%profana%' and title not like '%adulto%' " +
            "and title not like '%adultos%' and title not like '%pornhub%' " +
            "and title not like '%sexy%' and title not like '%censura%' " +
            "and title not like '%bikini%' and title not like '%+18%' " +
            "and title not like '%sensual%' and title not like '%agua%gamer%' " +
            "and title not like '%porno%' order by id desc limit 3")
    fun getFlowDataListCensuredLimited() : Flow<List<NewsItem>>
}